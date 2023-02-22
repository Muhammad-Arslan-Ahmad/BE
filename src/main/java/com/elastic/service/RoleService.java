package com.elastic.service;

import com.elastic.common.util.Utils;
import com.elastic.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.elastic.common.enums.IndexName;
import static com.elastic.common.enums.IndexName.INDEX_PLAY;
import com.elastic.common.enums.OperationEnum;
import com.elastic.common.enums.StatusEnum;
import com.elastic.payload.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.elastic.common.enums.IndexName.INDEX_ROLES;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;

@Slf4j
@Service
public class RoleService {
    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;

    private final Tika tika;

    private final  ElasticSearchService elasticSearchService;

    public RoleService(RestHighLevelClient client, ObjectMapper objectMapper, Tika tika, ElasticSearchService elasticSearchService) {
        this.client = client;
        this.objectMapper = objectMapper;
        this.tika = tika;
        this.elasticSearchService = elasticSearchService;
    }
    public CreateUpdateRoleResponse createUpdateRole(CreateUpdateRoleRequest request) {

        if(request == null) {
            return new CreateUpdateRoleResponse(false, "All input cannot be empty");
        }
        if (!Utils.isOk(request.getRoleName())) {
            return new CreateUpdateRoleResponse(false, "Role name is required");
        }
        if (request.getFileName() == null && request.getAttachment() != null) {
            String name = System.currentTimeMillis()+":"+tika.detect(request.getAttachment()).replace('/', '_');
            request.setFileName(name);
        }
        request.setCreatedAt(new Date());
        if(request.getStatus() == null || request.getStatus().length() == 0){
            request.setStatus(StatusEnum.DRAFT.name());
        }
        String id = elasticSearchService.indexDocument(request, Utils.getMd5(request.getRoleName().trim().toLowerCase()), INDEX_ROLES.getValue());
        if(id != null) {
            return new CreateUpdateRoleResponse(id, request.getRoleName(), request.getJobTitle(), request.getRoleAltitudeLevel(), request.getReportingRole(), request.getAttachment(), request.getExpectations(), request.getPainAndGains(), request.getPossibilities(), request.getImpactedWorks(), request.getHiddenPitfalls(), request.getCreatedAt(), request.getNarrations());
        }
        return new CreateUpdateRoleResponse(false, "Internal service error. Please contact with admin");
    }

    public GetRoleResponse getRole(String id) {
        if(id == null) {
            return new GetRoleResponse(false,"Role id is required");
        }

        GetRequest request = new GetRequest(INDEX_ROLES.getValue(), id);
        try {
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            if(response.isExists() && response.getSource() != null && response.getSource().size() >0) {
                Role role = objectMapper.readValue(response.getSourceAsString(), Role.class);
                role.setId(response.getId());
                return new GetRoleResponse(role);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return new GetRoleResponse(false, "No result found");
    }
    
    

    public GetSmallRoleResponse getSmallRole(GetSmallRoleRequest request) {
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
        sourceBuilder.fetchSource(new String[]{"id","roleName"}, null);
        SearchRequest searchRequest = new SearchRequest(INDEX_ROLES.getValue());
        searchRequest.source(sourceBuilder);

        Long totalCount = 0L;
        List<SmallRole> roles = new ArrayList<>();
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            if(response != null && response.getHits() != null && response.getHits().getHits() != null && response.getHits().getHits().length >0) {
                for (SearchHit hit : response.getHits().getHits()) {
                    SmallRole smallRole = new SmallRole();
                    if(hit.getSourceAsMap().get("id") != null) {
                        smallRole.setId((String) hit.getSourceAsMap().get("id"));
                    }
                    if(hit.getSourceAsMap().get("roleName") != null) {
                        smallRole.setName((String) hit.getSourceAsMap().get("roleName"));
                    }
                    roles.add(smallRole);
                }
                totalCount = response.getHits().getTotalHits().value;
            }
        } catch (Throwable t) {
            log.error("OPERATION:{} INDEX_NAME:{} MESSAGE:{}", OperationEnum.SEARCH.name(), INDEX_ROLES.name(),t.getMessage());
            return new GetSmallRoleResponse(false, "Internal service error. Please contact with admin");
        }
        return new GetSmallRoleResponse(roles, totalCount);
    }

    public RoleSearchResponse searchRole(RoleSearchRequest request) {
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
            queryBuilder.should(QueryBuilders.multiMatchQuery(request.getQuery().trim(),"expectations", "fileName", "hiddenPitfalls", "id", "impactedWorks", "jobTitle", "painAndGains", "possibilities", "reportingRole", "roleAltitudeLevel", "roleName").type(MultiMatchQueryBuilder.Type.PHRASE_PREFIX).operator(Operator.AND).boost(5.0f));
            queryBuilder.should(QueryBuilders.multiMatchQuery(request.getQuery().trim(),"expectations", "fileName", "hiddenPitfalls", "id", "impactedWorks", "jobTitle", "painAndGains", "possibilities", "reportingRole", "roleAltitudeLevel", "roleName").type(MultiMatchQueryBuilder.Type.PHRASE_PREFIX).operator(Operator.OR).boost(0.01f));
            criteriaFound = true;
        }
        //queryBuilder.must(QueryBuilders.matchQuery("status", StatusEnum.COMPLETED.name()));
        
//        if(!criteriaFound) {
//            queryBuilder.must(QueryBuilders.matchAllQuery());
//        }
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(request.getFrom());
        sourceBuilder.size(request.getSize());
        sourceBuilder.query(queryBuilder);
        sourceBuilder.sort(request.getColumn() == null ? "createdAt" : request.getColumn().getValue(), request.getSortOrder() == null ? SortOrder.DESC : (request.getSortOrder().equals(com.elastic.common.enums.SortOrder.DESC) ? SortOrder.DESC : SortOrder.ASC));
        sourceBuilder.trackTotalHits(true);
        sourceBuilder.fetchSource(null, "attachment");
        SearchRequest searchRequest = new SearchRequest(INDEX_ROLES.getValue());
        searchRequest.source(sourceBuilder);

        Long totalCount = 0L;
        List<Role> roles = new ArrayList<>();
        try {
            SearchResponse response = this.client.search(searchRequest, RequestOptions.DEFAULT);
            if(response != null && response.getHits() != null && response.getHits().getHits() != null && response.getHits().getHits().length >0) {
                for (SearchHit hit : response.getHits().getHits()) {
                    Role role = objectMapper.readValue(hit.getSourceAsString(), Role.class);
                    if(hit.getId() != null) {
                        role.setId(hit.getId());
                    }
                    roles.add(role);
                }
                totalCount = response.getHits().getTotalHits().value;
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return new RoleSearchResponse(false, "Internal service error. Please contact with admin!");
        }

        return new RoleSearchResponse(roles, totalCount);
    }


    public RoleSearchResponse globalSearch(GlobalSearchRequest request) {
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

        SearchRequest searchRequest = new SearchRequest(INDEX_ROLES.getValue());
        searchRequest.source(sourceBuilder);

        Long totalCount = 0L;
        List<Role> roles = new ArrayList<>();
        try {
            SearchResponse response = this.client.search(searchRequest, RequestOptions.DEFAULT);
            if (Utils.isOk(response)) {
                for (SearchHit hit : response.getHits().getHits()) {
                    if (hit == null) {
                        continue;
                    }

                    Role role = this.objectMapper.readValue(hit.getSourceAsString(), Role.class);
                    if (!Utils.isOk(role.getId())) {
                        role.setId(hit.getId());
                    }
                    roles.add(role);
                }
                totalCount = response.getHits().getTotalHits().value;
            }

        } catch (Throwable t) {
            log.error("Error:", t);
            return new RoleSearchResponse(false, "Internal service error. Please contact with admin!");
        }
        return new RoleSearchResponse(roles, totalCount);
    }
    
    public CommonDeleteResponse delete(CommonDeleteRequest request) {
        if (request == null) {
            return new CommonDeleteResponse(false);
        }

        if (!Utils.isOk(request.getId())) {
            return new CommonDeleteResponse(false);
        }

        try {
            DeleteRequest delRequest = new DeleteRequest(INDEX_ROLES.getValue(), request.getId());
            DeleteResponse deleteResponse = this.client.delete(
                    delRequest, RequestOptions.DEFAULT);

            return new CommonDeleteResponse(true);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new CommonDeleteResponse(false);
    }
}
