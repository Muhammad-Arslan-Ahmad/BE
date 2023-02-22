package com.elastic.payload;

import com.elastic.model.SmallProduct;
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
public class GetSmallProductResponse extends ServiceResponse implements Serializable {

    List<SmallProduct> products;
    Long totalCount;

    public GetSmallProductResponse(boolean status, String message) {
        super(status, message);
    }

    public GetSmallProductResponse(List<SmallProduct> products, Long totalCount) {
        super();
        this.products = products;
        this.totalCount = totalCount;
    }
}
