package com.elastic.payload;

import com.elastic.model.Role;
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
public class RoleSearchResponse extends ServiceResponse implements Serializable {
    List<Role> roles;
    Long totalCount;

    public RoleSearchResponse(boolean status, String message) {
        super(status, message);
    }

    public RoleSearchResponse(List<Role> roles, Long totalCount) {
        super();
        this.roles = roles;
        this.totalCount = totalCount;
    }
}
