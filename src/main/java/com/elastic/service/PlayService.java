package com.elastic.service;

import com.elastic.common.enums.IndexName;
import com.elastic.common.enums.OperationEnum;
import com.elastic.common.enums.StatusEnum;
import com.elastic.common.util.Utils;
import com.elastic.model.Play;
import com.elastic.model.SmallPlay;
import com.elastic.payload.*;
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

import static com.elastic.common.enums.IndexName.*;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;

@Slf4j
@Service
public class PlayService {
    private static final Logger LOGGER = LogManager.getLogger(PlayService.class);
    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;

    private final Tika tika;

    private final ElasticSearchService elasticSearchService;

    public PlayService(RestHighLevelClient client, ObjectMapper objectMapper, Tika tika, ElasticSearchService elasticSearchService) {
        this.client = client;
        this.objectMapper = objectMapper;
        this.tika = tika;
        this.elasticSearchService = elasticSearchService;
    }

    public CreateUpdatePlayResponse createUpdatePlay(CreateUpdatePlayRequest request) {
        if(request == null) {
            return new CreateUpdatePlayResponse(false, "All input cannot be empty");
        }
        if (!Utils.isOk(request.getPlayName())) {
            return new CreateUpdatePlayResponse(false, "Play name is required");
        }
        if (request.getFileName() == null && request.getAttachment() != null) {
            String name = System.currentTimeMillis()+":"+tika.detect(request.getAttachment()).replace('/', '_');
            request.setFileName(name);
        }
        request.setCreatedAt(new Date());
        if(request.getStatus() == null || request.getStatus().length() == 0){
            request.setStatus(StatusEnum.DRAFT.name());
        }
        if(Utils.isOk(request.getPlayStartStr())){
            request.setPlayStart(Utils.getDate(request.getPlayStartStr()));
        }
        if(Utils.isOk(request.getPlayEndStr())){
            request.setPlayEnd(Utils.getDate(request.getPlayEndStr()));
        }
        if(request.getPlayProductNames() != null && request.getPlayProductNames().size() >0) {
            for (String productName : request.getPlayProductNames()) {
                if(productName == null || productName.trim().length() ==0) {
                    continue;
                }
                CreateUpdateProductRequest createUpdateProductRequest = new CreateUpdateProductRequest();
                createUpdateProductRequest.setName(productName);
                createUpdateProductRequest.setStatus(request.getStatus());
                createUpdateProductRequest.setCreatedAt(new Date());
                if (!elasticSearchService.isExists(Utils.getMd5(productName.trim().toLowerCase()), INDEX_PRODUCT.getValue())) {
                    elasticSearchService.indexDocument(createUpdateProductRequest, Utils.getMd5(productName.trim().toLowerCase()), INDEX_PRODUCT.getValue());
                }
            }
        }
        if (request.getPlayRoles() != null && request.getPlayRoles().size() >0) {
            for (String roleName : request.getPlayRoles()) {
                if (roleName == null || roleName.trim().length() == 0) {
                    continue;
                }
                CreateUpdateRoleRequest roleRequest = new CreateUpdateRoleRequest();
                roleRequest.setRoleName(roleName);
                roleRequest.setCreatedAt(new Date());
                roleRequest.setStatus(request.getStatus());
                if (!elasticSearchService.isExists(Utils.getMd5(roleName.trim().toLowerCase()), INDEX_ROLES.getValue())) {
                    elasticSearchService.indexDocument(roleRequest, Utils.getMd5(roleName.trim().toLowerCase()), INDEX_ROLES.getValue());
                }
            }
        }

        if(request.getPlayIndustry() != null && request.getPlayIndustry().size() >0) {
            for (String industryName : request.getPlayIndustry()) {
                if(industryName == null || industryName.trim().length() ==0) {
                    continue;
                }
                CreateUpdateIndustryRequest industryRequest = new CreateUpdateIndustryRequest();
                industryRequest.setName(industryName);
                industryRequest.setCreatedAt(new Date());
                industryRequest.setStatus(request.getStatus());
                if (!elasticSearchService.isExists(Utils.getMd5(industryName.trim().toLowerCase()), INDEX_INDUSTRY.getValue())) {
                    elasticSearchService.indexDocument(industryRequest, Utils.getMd5(industryName.trim().toLowerCase()), INDEX_INDUSTRY.getValue());
                }
            }
        }

        String id = elasticSearchService.indexDocument(request, Utils.getMd5(request.getPlayName().trim().toLowerCase()), INDEX_PLAY.getValue());
        if(id != null) {
            return new CreateUpdatePlayResponse(id, request.getPlayName(), request.getAttachment(), request.getFileName(),
                    request.getContentType(), request.getPlayBusinessOutcome(), request.getPlayIndustry(),
                    request.getPlaySolutionType(), request.getPlayProductCategory(), request.getPlayProductNames(),
                    request.getPlayRoles(),
                    request.getPlayStart(),request.getPlayEnd(),request.getPlayWinThemes(),
                    request.getCreatedAt(), request.getStatus(), request.getPlayStartStr(), request.getPlayEndStr(), request.getNarrations());
        }
        return new CreateUpdatePlayResponse(false, "Internal service error. Please contact with admin");
    }

    public GetPlayResponse getPlay(String id) {
        if(id == null) {
            return new GetPlayResponse(false,"Play id is required");
        }

        GetRequest request = new GetRequest(IndexName.INDEX_PLAY.getValue(), id);
        try {
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            if(response.isExists() && response.getSource() != null && response.getSource().size() >0) {
                Play play = objectMapper.readValue(response.getSourceAsString(), Play.class);
                play.setId(response.getId());
                return new GetPlayResponse(play);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return new GetPlayResponse(false, "No result found");
    }

    public GetSmallPlayResponse getSmallPlay(GetSmallPlayRequest request) {
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
        sourceBuilder.fetchSource(new String[]{"id","playName"}, null);
        SearchRequest searchRequest = new SearchRequest(INDEX_PLAY.getValue());
        searchRequest.source(sourceBuilder);

        Long totalCount = 0L;
        List<SmallPlay> plays = new ArrayList<>();
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            if(response != null && response.getHits() != null && response.getHits().getHits() != null && response.getHits().getHits().length >0) {
                for (SearchHit hit : response.getHits().getHits()) {
                    SmallPlay smallPay = new SmallPlay();
                    if(hit.getSourceAsMap().get("id") != null) {
                        smallPay.setId((String) hit.getSourceAsMap().get("id"));
                    }
                    if(hit.getSourceAsMap().get("playName") != null) {
                        smallPay.setName((String) hit.getSourceAsMap().get("playName"));
                    }
                    plays.add(smallPay);
                }
                totalCount = response.getHits().getTotalHits().value;
            }
        } catch (Throwable t) {
            LOGGER.error("OPERATION:{} INDEX_NAME:{} MESSAGE:{}", OperationEnum.SEARCH.name(), INDEX_PLAY.name(),t.getMessage());
            return new GetSmallPlayResponse(false, "Internal service error. Please contact with admin");
        }
        return new GetSmallPlayResponse(plays, totalCount);
    }

    public SearchPlayResponse searchPlay(SearchPlayRequest request) {
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
        SearchRequest searchRequest = new SearchRequest(INDEX_PLAY.getValue());
        searchRequest.source(sourceBuilder);

        Long totalCount = 0L;
        List<Play> plays = new ArrayList<>();
        try {
            SearchResponse response = this.client.search(searchRequest, RequestOptions.DEFAULT);
            if(Utils.isOk(response)) {
                for (SearchHit hit : response.getHits().getHits()) {
                    Play play = objectMapper.readValue(hit.getSourceAsString(), Play.class);
                    if(hit.getId() != null) {
                        play.setId(hit.getId());
                    }
                    plays.add(play);
                }
                totalCount = response.getHits().getTotalHits().value;
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return new SearchPlayResponse(false, "Internal service error. Please contact with admin!");
        }

        return new SearchPlayResponse(plays, totalCount);
    }

    public SearchPlayResponse globalSearch(GlobalSearchRequest request) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.trackTotalHits(true);
        sourceBuilder.query(Utils.getGlobalSearchQuery(request.getQuery()));
        sourceBuilder.from(request.getFrom());
        sourceBuilder.size(request.getSize());
        sourceBuilder.fetchSource(null, new String[] {"attachment", "file"});

        SearchRequest searchRequest = new SearchRequest(INDEX_PLAY.getValue());
        searchRequest.source(sourceBuilder);

        Long totalCount = 0L;
        List<Play> plays = new ArrayList<>();
        try {
            SearchResponse response = this.client.search(searchRequest, RequestOptions.DEFAULT);
            if (Utils.isOk(response)) {
                for (SearchHit hit : response.getHits().getHits()) {
                    if (hit == null) {
                        continue;
                    }

                    Play play = this.objectMapper.readValue(hit.getSourceAsString(), Play.class);
                    if (!Utils.isOk(play.getId())) {
                        play.setId(hit.getId());
                    }
                    plays.add(play);
                }
                totalCount = response.getHits().getTotalHits().value;
            }

        } catch (Throwable t) {
            log.error("Error:", t);
            return new SearchPlayResponse(false, "Internal service error. Please contact with admin!");
        }
        return new SearchPlayResponse(plays, totalCount);
    }
    
    public CommonDeleteResponse delete(CommonDeleteRequest request) {
        if (request == null) {
            return new CommonDeleteResponse(false);
        }

        if (!Utils.isOk(request.getId())) {
            return new CommonDeleteResponse(false);
        }

        try {
            DeleteRequest delRequest = new DeleteRequest(INDEX_PLAY.getValue(), request.getId());
            DeleteResponse deleteResponse = this.client.delete(
                    delRequest, RequestOptions.DEFAULT);

            return new CommonDeleteResponse(true);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new CommonDeleteResponse(false);
    }
}
