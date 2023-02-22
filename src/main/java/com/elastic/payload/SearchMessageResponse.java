package com.elastic.payload;

import com.elastic.model.Message;
import com.elastic.security.payload.ServiceResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchMessageResponse extends ServiceResponse implements Serializable {

    List<Message> messages;
    Long totalCount;

    public SearchMessageResponse(boolean status, String message) {
        super(status, message);
    }

    public SearchMessageResponse(List<Message> messages, Long totalCount) {
        super();
        this.messages = messages;
        this.totalCount = totalCount;
    }
}
