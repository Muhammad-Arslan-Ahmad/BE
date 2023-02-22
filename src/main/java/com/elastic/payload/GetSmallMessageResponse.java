package com.elastic.payload;

import com.elastic.model.SmallMessage;
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
public class GetSmallMessageResponse extends ServiceResponse implements Serializable {

    List<SmallMessage> messages;
    Long totalCount;

    public GetSmallMessageResponse(boolean status, String message) {
        super(status, message);
    }

    public GetSmallMessageResponse(List<SmallMessage> messages, Long totalCount) {
        super();
        this.messages = messages;
        this.totalCount = totalCount;
    }
}
