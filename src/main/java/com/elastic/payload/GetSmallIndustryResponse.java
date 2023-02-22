package com.elastic.payload;

import com.elastic.model.SmallIndustry;
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
public class GetSmallIndustryResponse extends ServiceResponse implements Serializable {

    List<SmallIndustry> industries;
    Long totalCount;

    public GetSmallIndustryResponse(boolean status, String message) {
        super(status, message);
    }

    public GetSmallIndustryResponse(List<SmallIndustry> industries, Long totalCount) {
        super();
        this.industries = industries;
        this.totalCount = totalCount;
    }
}
