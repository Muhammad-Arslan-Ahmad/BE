package com.elastic.payload;

import com.elastic.model.Play;
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
public class SearchPlayResponse extends ServiceResponse implements Serializable {

    List<Play> plays;
    Long totalCount;

    public SearchPlayResponse(boolean status, String message) {
        super(status, message);
    }

    public SearchPlayResponse(List<Play> plays, Long totalCount) {
        super();
        this.plays = plays;
        this.totalCount = totalCount;
    }
}
