package com.elastic.payload;

import com.elastic.model.Proof;
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
public class ProofSearchResponse extends ServiceResponse implements Serializable {

    List<Proof> result;
    Long totalCount;

    public ProofSearchResponse(boolean status, String message) {
        super(status, message);
    }

    public ProofSearchResponse(List<Proof> result, Long totalCount) {
        super();
        this.result = result;
        this.totalCount = totalCount;
    }
}
