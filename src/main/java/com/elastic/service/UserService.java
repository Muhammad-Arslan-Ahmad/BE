package com.elastic.service;

import com.elastic.common.enums.IndexName;
import com.elastic.common.enums.OperationEnum;
import com.elastic.common.enums.StatusEnum;
import com.elastic.common.util.Utils;
import com.elastic.model.SmallUser;
import com.elastic.model.User;
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

import static com.elastic.common.enums.IndexName.INDEX_INDUSTRY;
import static com.elastic.common.enums.IndexName.INDEX_USER;

@Slf4j
@Service
public class UserService {
    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;

    private final Tika tika;

    private final  ElasticSearchService elasticSearchService;

    public UserService(RestHighLevelClient client, ObjectMapper objectMapper, Tika tika, ElasticSearchService elasticSearchService) {
        this.client = client;
        this.objectMapper = objectMapper;
        this.tika = tika;
        this.elasticSearchService = elasticSearchService;
    }


    public CreateUpdateUserResponse createUpdateUser(CreateUpdateUserRequest request) {
        if(request == null) {
            return new CreateUpdateUserResponse(false, "All input cannot be empty");
        }
        if (request.getFileName() == null && request.getPhoto() != null) {
            String name = System.currentTimeMillis()+":"+tika.detect(request.getPhoto()).replace('/', '_');
            request.setFileName(name);
        }
        request.setCreatedAt(new Date());
        if(request.getStatus() == null || request.getStatus().length() == 0){
            request.setStatus(StatusEnum.DRAFT.name());
        }
        String id = elasticSearchService.indexDocument(request, request.getId(), INDEX_USER.getValue());
        if(id != null) {
            return new CreateUpdateUserResponse(id, request.getUserName(), request.getPassword(), request.getPhoto(),
                    request.getContentType(), request.getScheduler(), request.getUserSenderOrganization(),
                    request.getUserFirstName(), request.getUserLastName(), request.getUserPhone(),
                    request.getCreatedAt(), request.getStatus());
        }
        return new CreateUpdateUserResponse(false, "Internal service error. Please contact with admin");
    }

    public GetUserResponse getUser(String id) {
        if(id == null) {
            return new GetUserResponse(false,"Product id is required");
        }

        GetRequest request = new GetRequest(INDEX_USER.getValue(), id);
        try {
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            if(response.isExists() && response.getSource() != null && response.getSource().size() >0) {
                User user = objectMapper.readValue(response.getSourceAsString(), User.class);
                user.setId(response.getId());
                return new GetUserResponse(user);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return new GetUserResponse(false, "No result found");
    }

    public GetSmallUserResponse getSmallUser(GetSmallUserRequest request) {
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
        sourceBuilder.fetchSource(new String[]{"id","userFirstName", "userLastName"}, null);
        SearchRequest searchRequest = new SearchRequest(INDEX_USER.getValue());
        searchRequest.source(sourceBuilder);

        Long totalCount = 0L;
        List<SmallUser> users = new ArrayList<>();
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            if(response != null && response.getHits() != null && response.getHits().getHits() != null && response.getHits().getHits().length >0) {
                for (SearchHit hit : response.getHits().getHits()) {
                    SmallUser smallUser = new SmallUser();
                    if(hit.getSourceAsMap().get("id") != null) {
                        smallUser.setId((String) hit.getSourceAsMap().get("id"));
                    }
                    String name = "";
                    if(hit.getSourceAsMap().get("userFirstName") != null) {
                        name += " "+hit.getSourceAsMap().get("userFirstName");
                    }
                    if(hit.getSourceAsMap().get("userLastName") != null) {
                        name += hit.getSourceAsMap().get("userLastName");
                    }
                    smallUser.setName(name);
                    users.add(smallUser);
                }
                totalCount = response.getHits().getTotalHits().value;
            }
        } catch (Throwable t) {
            log.error("OPERATION:{} INDEX_NAME:{} MESSAGE:{}", OperationEnum.SEARCH.name(), INDEX_USER.name(),t.getMessage());
            return new GetSmallUserResponse(false, "Internal service error. Please contact with admin");
        }
        return new GetSmallUserResponse(users, totalCount);
    }

    public SearchUserResponse searchUser(SearchUserRequest request) {
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
        sourceBuilder.fetchSource(null, "photo");
        SearchRequest searchRequest = new SearchRequest(INDEX_USER.getValue());
        searchRequest.source(sourceBuilder);

        Long totalCount = 0L;
        List<User> users = new ArrayList<>();
        try {
            SearchResponse response = this.client.search(searchRequest, RequestOptions.DEFAULT);
            if(response != null && response.getHits() != null && response.getHits().getHits() != null && response.getHits().getHits().length >0) {
                for (SearchHit hit : response.getHits().getHits()) {
                    User user = objectMapper.readValue(hit.getSourceAsString(), User.class);
                    users.add(user);
                }
                totalCount = response.getHits().getTotalHits().value;
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return new SearchUserResponse(false, "Internal service error. Please contact with admin!");
        }

        return new SearchUserResponse(users, totalCount);
    }

    public SearchUserResponse globalSearch(GlobalSearchRequest request) {
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

        SearchRequest searchRequest = new SearchRequest(INDEX_USER.getValue());
        searchRequest.source(sourceBuilder);

        Long totalCount = 0L;
        List<User> users = new ArrayList<>();
        try {
            SearchResponse response = this.client.search(searchRequest, RequestOptions.DEFAULT);
            if (Utils.isOk(response)) {
                for (SearchHit hit : response.getHits().getHits()) {
                    if (hit == null) {
                        continue;
                    }

                    User user = this.objectMapper.readValue(hit.getSourceAsString(), User.class);
                    if (!Utils.isOk(user.getId())) {
                        user.setId(hit.getId());
                    }
                    users.add(user);
                }
                totalCount = response.getHits().getTotalHits().value;
            }

        } catch (Throwable t) {
            log.error("Error:", t);
            return new SearchUserResponse(false, "Internal service error. Please contact with admin!");
        }
        return new SearchUserResponse(users, totalCount);
    }
}
