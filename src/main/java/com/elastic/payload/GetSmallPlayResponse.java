package com.elastic.payload;

import com.elastic.model.SmallPlay;
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
public class GetSmallPlayResponse extends ServiceResponse implements Serializable {

    List<SmallPlay> plays;
    Long totalCount;

    public GetSmallPlayResponse(boolean status, String message) {
        super(status, message);
    }

    public GetSmallPlayResponse(List<SmallPlay> plays, Long totalCount) {
        super();
        this.plays = plays;
        this.totalCount = totalCount;
    }
}
