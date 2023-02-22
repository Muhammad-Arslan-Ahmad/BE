package com.elastic.service;

import com.elastic.common.enums.IndexName;
import static com.elastic.common.enums.IndexName.INDEX_PLAY;
import com.elastic.common.enums.OperationEnum;
import com.elastic.common.util.Utils;
import com.elastic.model.Product;
import com.elastic.model.Proof;
import com.elastic.payload.GlobalSearchRequest;
import com.elastic.payload.ProofSearchRequest;
import com.elastic.payload.ProofSearchResponse;
import com.elastic.payload.SearchProductResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.elastic.common.enums.IndexName.INDEX_PROOF;
import com.elastic.common.enums.StatusEnum;
import com.elastic.payload.CommonDeleteRequest;
import com.elastic.payload.CommonDeleteResponse;
import java.util.Date;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;

@Slf4j
@Service
public class ProofService {
    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;

    private final Tika tika;
    private final ElasticSearchService elasticSearchService;

    public ProofService(RestHighLevelClient client, ObjectMapper objectMapper, Tika tika, ElasticSearchService elasticSearchService) {
        this.client = client;
        this.objectMapper = objectMapper;
        this.tika = tika;
        this.elasticSearchService = elasticSearchService;
    }

    public Proof createUpdateProof(Proof request) {
        String id = null;
        if(request.getId() != null) {
            id = request.getId();
        }
        if (request.getFileName() == null && request.getAttachment() != null) {
            String name = System.currentTimeMillis()+":"+tika.detect(request.getAttachment()).replace('/', '_');
            request.setFileName(name);
        }
        request.setCreatedAt(new Date());
        if(request.getStatus() == null || request.getStatus().length() == 0){
            request.setStatus(StatusEnum.DRAFT.name());
        }
        String responseId = elasticSearchService.indexDocument(request, id, INDEX_PROOF.getValue());
        request.setId(responseId);
        return request;
    }

    public Proof getProof(String id) {

        Proof proof = new Proof();
        GetRequest request = new GetRequest(INDEX_PROOF.getValue(), id);
        try {
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            if(response.isExists() && response.getSource() != null && response.getSource().size() >0) {
                proof = objectMapper.readValue(response.getSourceAsString(), Proof.class);
                proof.setId(response.getId());

            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return proof;
    }

    public ProofSearchResponse searchProof(ProofSearchRequest request) {

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if(request.getQuery() == null) {
            boolQueryBuilder.must(QueryBuilders.matchAllQuery());
        } else {
            boolQueryBuilder.must(QueryBuilders.queryStringQuery(request.getQuery().trim()).defaultOperator(Operator.OR));
        }

        if(request.getSize() == null || request.getSize() <=0) {
            request.setSize(10);
        }
        if(request.getFrom() == null || request.getFrom() <0) {
            request.setFrom(0);
        }

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.from(request.getFrom());
        sourceBuilder.size(request.getSize());
        sourceBuilder.trackTotalHits(true);
        SearchRequest searchRequest = new SearchRequest(INDEX_PROOF.getValue());
        searchRequest.source(sourceBuilder);
        List<Proof> proofs = new ArrayList<>();
        Long totalCount = 0L;
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            if(response != null && response.getHits() != null && response.getHits().getHits() != null && response.getHits().getHits().length >0) {
                for (SearchHit hit : response.getHits().getHits()) {
                    Proof proof = objectMapper.readValue(hit.getSourceAsString(), Proof.class);
                    if(hit.getId() != null) {
                        proof.setId(hit.getId());
                    }
                    proofs.add(proof);
                }
                totalCount = response.getHits().getTotalHits().value;
            }
        } catch (Throwable t) {
            log.error("OPERATION:{} INDEX_NAME:{} MESSAGE:{}", OperationEnum.SEARCH.name(), INDEX_PROOF.name(),t.getMessage());
            return new ProofSearchResponse(false, "Internal service error. Please contact with admin");
        }
        return new ProofSearchResponse(proofs, totalCount);
    }

    public ProofSearchResponse globalSearch(GlobalSearchRequest request) {
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

        SearchRequest searchRequest = new SearchRequest(INDEX_PROOF.getValue());
        searchRequest.source(sourceBuilder);

        Long totalCount = 0L;
        List<Proof> proofs = new ArrayList<>();
        try {
            SearchResponse response = this.client.search(searchRequest, RequestOptions.DEFAULT);
            if (Utils.isOk(response)) {
                for (SearchHit hit : response.getHits().getHits()) {
                    if (hit == null) {
                        continue;
                    }

                    Proof proof = this.objectMapper.readValue(hit.getSourceAsString(), Proof.class);
                    if (!Utils.isOk(proof.getId())) {
                        proof.setId(hit.getId());
                    }
                    proofs.add(proof);
                }
                totalCount = response.getHits().getTotalHits().value;
            }

        } catch (Throwable t) {
            log.error("Error:", t);
            return new ProofSearchResponse(false, "Internal service error. Please contact with admin!");
        }
        return new ProofSearchResponse(proofs, totalCount);
    }
    
    public CommonDeleteResponse delete(CommonDeleteRequest request) {
        if (request == null) {
            return new CommonDeleteResponse(false);
        }

        if (!Utils.isOk(request.getId())) {
            return new CommonDeleteResponse(false);
        }

        try {
            DeleteRequest delRequest = new DeleteRequest(INDEX_PROOF.getValue(), request.getId());
            DeleteResponse deleteResponse = this.client.delete(
                    delRequest, RequestOptions.DEFAULT);

            return new CommonDeleteResponse(true);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new CommonDeleteResponse(false);
    }
}
