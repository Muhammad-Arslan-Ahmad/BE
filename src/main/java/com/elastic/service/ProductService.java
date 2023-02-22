package com.elastic.service;

import com.elastic.common.enums.IndexName;
import static com.elastic.common.enums.IndexName.INDEX_PLAY;
import com.elastic.common.enums.OperationEnum;
import com.elastic.common.enums.StatusEnum;
import com.elastic.common.util.Utils;
import com.elastic.model.Product;
import com.elastic.model.SmallProduct;
import com.elastic.payload.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.elastic.common.enums.IndexName.INDEX_PRODUCT;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;

@Slf4j
@Service
public class ProductService {
    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;

    private final Tika tika;

    private final ElasticSearchService elasticSearchService;

    public ProductService(RestHighLevelClient client, ObjectMapper objectMapper, Tika tika, ElasticSearchService elasticSearchService) {
        this.client = client;
        this.objectMapper = objectMapper;
        this.tika = tika;
        this.elasticSearchService = elasticSearchService;
    }

    public CreateUpdateProductResponse createUpdateProduct(CreateUpdateProductRequest request) {
        if(request == null) {
            return new CreateUpdateProductResponse(false, "All input cannot be empty");
        }
        if (!Utils.isOk(request.getName())) {
            return new CreateUpdateProductResponse(false, "Product name is required");
        }
        if (request.getFileName() == null && request.getAttachment() != null) {
            String name = System.currentTimeMillis()+":"+tika.detect(request.getAttachment()).replace('/', '_');
            request.setFileName(name);
        }
        request.setCreatedAt(new Date());
        if(request.getStatus() == null || request.getStatus().length() == 0){
            request.setStatus(StatusEnum.DRAFT.name());
        }
        String id = elasticSearchService.indexDocument(request, Utils.getMd5(request.getName().trim().toLowerCase()), INDEX_PRODUCT.getValue());
        if(id != null) {
            return new CreateUpdateProductResponse(id, request.getName(), request.getAttachment(), request.getFileName(),
                    request.getContentType(), request.getProductFeatures(), request.isProductFeatureAltitude(),
                    request.getProductNameSelected(), request.getProductCategorySelected(), request.getProductBenefits(),
                    request.isProductBenefitAdvantage(), request.getProductCapabilities(),
                    request.isProductCapabilityHighlight(),request.getProductRisks(),request.getProofNames(),
                    request.getCreatedAt(), request.getStatus(), request.getNarrations());
        }
        return new CreateUpdateProductResponse(false, "Internal service error. Please contact with admin");
    }

    public GetProductResponse getProduct(String id) {
        if(id == null) {
            return new GetProductResponse(false,"Product id is required");
        }

        GetRequest request = new GetRequest(INDEX_PRODUCT.getValue(), id);
        try {
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            if(response.isExists() && response.getSource() != null && response.getSource().size() >0) {
                Product product = objectMapper.readValue(response.getSourceAsString(), Product.class);
                product.setId(response.getId());
                return new GetProductResponse(product);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return new GetProductResponse(false, "No result found");
    }

    public GetSmallProductResponse getSmallProduct(GetSmallProductRequest request) {
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        if(request.getId() != null) {
            queryBuilder.must(QueryBuilders.termQuery("_id", request.getId().trim()));
        } else {
            queryBuilder.must(QueryBuilders.matchAllQuery());
        }

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(0);
        sourceBuilder.size(10000);
        sourceBuilder.query(queryBuilder);
        sourceBuilder.trackTotalHits(true);
        sourceBuilder.fetchSource(new String[]{"id","name"}, null);
        SearchRequest searchRequest = new SearchRequest(INDEX_PRODUCT.getValue());
        searchRequest.source(sourceBuilder);

        Long totalCount = 0L;
        List<SmallProduct> products = new ArrayList<>();
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            if(Utils.isOk(response)) {
                for (SearchHit hit : response.getHits().getHits()) {
                    SmallProduct smallProduct = new SmallProduct();
                    if(hit.getSourceAsMap().get("id") != null) {
                        smallProduct.setId((String) hit.getSourceAsMap().get("id"));
                    }
                    if(hit.getSourceAsMap().get("name") != null) {
                        smallProduct.setName((String) hit.getSourceAsMap().get("name"));
                    }
                    products.add(smallProduct);
                }
                totalCount = response.getHits().getTotalHits().value;
            }
        } catch (Throwable t) {
            log.error("OPERATION:{} INDEX_NAME:{} MESSAGE:{}", OperationEnum.SEARCH.name(), IndexName.INDEX_INDUSTRY.name(),t.getMessage());
            return new GetSmallProductResponse(false, "Internal service error. Please contact with admin");
        }
        return new GetSmallProductResponse(products, totalCount);
    }

    public SearchProductResponse searchProduct(SearchProductRequest request) {
        if(request.getFrom() == null || request.getFrom() <0) {
            request.setFrom(0);
        }
        if(request.getSize() == null || request.getSize() <0) {
            request.setSize(10);
        }

        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        boolean criteriaFound = false;
        if(request.getId() != null) {
            queryBuilder.must(QueryBuilders.termQuery("_id", request.getId().trim()));
            criteriaFound = true;
        }
        if(request.getQuery() != null && request.getQuery().length() >0) {
            queryBuilder.should(QueryBuilders.multiMatchQuery(request.getQuery().trim(),"*").type(MultiMatchQueryBuilder.Type.PHRASE_PREFIX).operator(Operator.AND).boost(5.0f));
            queryBuilder.should(QueryBuilders.multiMatchQuery(request.getQuery().trim(),"*").type(MultiMatchQueryBuilder.Type.PHRASE_PREFIX).operator(Operator.OR).boost(0.01f));
            criteriaFound = true;
        }
        //queryBuilder.must(QueryBuilders.matchQuery("status", StatusEnum.COMPLETED.name()));
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(request.getFrom());
        sourceBuilder.size(request.getSize());
        sourceBuilder.query(queryBuilder);
        sourceBuilder.sort(request.getColumn() == null ? "createdAt" : request.getColumn().getValue(), request.getSortOrder() == null ? SortOrder.DESC : (request.getSortOrder().equals(com.elastic.common.enums.SortOrder.DESC) ? SortOrder.DESC : SortOrder.ASC));
        sourceBuilder.trackTotalHits(true);
        sourceBuilder.fetchSource(null, "attachment");
        SearchRequest searchRequest = new SearchRequest(INDEX_PRODUCT.getValue());
        searchRequest.source(sourceBuilder);

        Long totalCount = 0L;
        List<Product> products = new ArrayList<>();
        try {
            SearchResponse response = this.client.search(searchRequest, RequestOptions.DEFAULT);
            if(Utils.isOk(response)) {
                for (SearchHit hit : response.getHits().getHits()) {
                    Product product = objectMapper.readValue(hit.getSourceAsString(), Product.class);
                    if(hit.getId() != null) {
                        product.setId(hit.getId());
                    }
                    products.add(product);
                }
                totalCount = response.getHits().getTotalHits().value;
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return new SearchProductResponse(false, "Internal service error. Please contact with admin!");
        }

        return new SearchProductResponse(products, totalCount);
    }

    public SearchProductResponse globalSearch(GlobalSearchRequest request) {
        if (request.getFrom() == null || request.getFrom() <0) {
            request.setFrom(0);
        }
        if (request.getSize() == null || request.getSize() <0 || request.getSize() >10000) {
            request.setSize(10);
        }

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.trackTotalHits(true);
        sourceBuilder.query(Utils.getGlobalSearchQuery(request.getQuery()));
        sourceBuilder.from(request.getFrom());
        sourceBuilder.size(request.getSize());
        sourceBuilder.fetchSource(null, new String[] {"attachment", "file"});

        SearchRequest searchRequest = new SearchRequest(INDEX_PRODUCT.getValue());
        searchRequest.source(sourceBuilder);

        Long totalCount = 0L;
        List<Product> products = new ArrayList<>();
        try {
            SearchResponse response = this.client.search(searchRequest, RequestOptions.DEFAULT);
            if (Utils.isOk(response)) {
                for (SearchHit hit : response.getHits().getHits()) {
                    if (hit == null) {
                        continue;
                    }

                    Product product = this.objectMapper.readValue(hit.getSourceAsString(), Product.class);
                    if (!Utils.isOk(product.getId())) {
                        product.setId(hit.getId());
                    }
                    products.add(product);
                }
                totalCount = response.getHits().getTotalHits().value;
            }

        } catch (Throwable t) {
            log.error("Error:", t);
            return new SearchProductResponse(false, "Internal service error. Please contact with admin!");
        }
        return new SearchProductResponse(products, totalCount);
    }
    
    public CommonDeleteResponse delete(CommonDeleteRequest request) {
        if (request == null) {
            return new CommonDeleteResponse(false);
        }

        if (!Utils.isOk(request.getId())) {
            return new CommonDeleteResponse(false);
        }

        try {
            DeleteRequest delRequest = new DeleteRequest(INDEX_PRODUCT.getValue(), request.getId());
            DeleteResponse deleteResponse = this.client.delete(
                    delRequest, RequestOptions.DEFAULT);

            return new CommonDeleteResponse(true);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new CommonDeleteResponse(false);
    }
}
