package com.elastic.payload;

import com.elastic.model.Industry;
import com.elastic.security.payload.ServiceResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class SearchIndustryResponse extends ServiceResponse implements Serializable {

    List<Industry> industries;
    Long totalCount;

    public SearchIndustryResponse(boolean status, String message) {
        super(status, message);
    }
}
