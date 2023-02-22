package com.elastic.service;

import com.elastic.common.enums.OperationEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ElasticSearchService {

    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;

    public ElasticSearchService(RestHighLevelClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public String indexDocument(Object data, String id, String indexName) {
        try {
            IndexRequest indexRequest = new IndexRequest(indexName);
            if (id != null) {
                indexRequest.id(id);
            }
            indexRequest.source(this.objectMapper.writeValueAsString(data), XContentType.JSON);
            //indexRequest.setPipeline("attachment");
            IndexResponse response = this.client.index(indexRequest, RequestOptions.DEFAULT);
            log.info("==PRODUCT INDEX RESULT:{}", response.status());
            return response.getId();
        } catch (Throwable t) {
            log.error("OPERATION:{} INDEX_NAME:{} MESSAGE:{}", OperationEnum.CREATE.name(), indexName,t.getMessage());
            return null;
        }
    }

    public boolean isExists(String id, String indexName) {
        GetRequest request = new GetRequest(indexName);
        request.id(id);
        request.fetchSourceContext(new FetchSourceContext(false));
        try {
            GetResponse response = this.client.get(request, RequestOptions.DEFAULT);
            return response.isExists();
        } catch (Throwable t) {
            log.error("ERROR:", t);
        }
        return false;
    }
}
