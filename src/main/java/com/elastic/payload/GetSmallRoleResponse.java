package com.elastic.payload;

import com.elastic.model.SmallRole;
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
public class GetSmallRoleResponse extends ServiceResponse implements Serializable {
    List<SmallRole> roles;
    Long totalCount;

    public GetSmallRoleResponse(boolean status, String message) {
        super(status, message);
    }

    public GetSmallRoleResponse(List<SmallRole> roles, Long totalCount) {
        super();
        this.roles = roles;
        this.totalCount = totalCount;
    }
}
