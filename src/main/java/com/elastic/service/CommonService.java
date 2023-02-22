package com.elastic.service;

import com.elastic.common.enums.IndexName;
import com.elastic.common.enums.OperationStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CommonService {
    private final RestHighLevelClient client;

    public CommonService(RestHighLevelClient client) {
        this.client = client;
    }

    public String clearIndex() {
        for (IndexName indexName : IndexName.values()) {
            try {
                DeleteIndexRequest request = new DeleteIndexRequest(indexName.getValue());
                AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
                log.info("DELETE :{} STATUS:{} ", indexName.getValue(), response.isAcknowledged());
            } catch (Throwable t) {
                log.error("ERROR:", t);
            }
        }

        return OperationStatusEnum.SUCCESS.name();
    }
}
