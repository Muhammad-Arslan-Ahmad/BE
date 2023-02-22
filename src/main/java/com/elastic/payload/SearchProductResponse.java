package com.elastic.payload;

import com.elastic.model.Product;
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
public class SearchProductResponse extends ServiceResponse implements Serializable {

    List<Product> products;
    Long totalCount;

    public SearchProductResponse(boolean status, String message) {
        super(status, message);
    }

    public SearchProductResponse(List<Product> products, Long totalCount) {
        super();
        this.products = products;
        this.totalCount = totalCount;
    }
}
