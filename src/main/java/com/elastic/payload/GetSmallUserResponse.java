package com.elastic.payload;

import com.elastic.model.SmallUser;
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
public class GetSmallUserResponse extends ServiceResponse implements Serializable {

    List<SmallUser> users;
    Long totalCount;

    public GetSmallUserResponse(boolean status, String message) {
        super(status, message);
    }

    public GetSmallUserResponse(List<SmallUser> users, Long totalCount) {
        super();
        this.users = users;
        this.totalCount = totalCount;
    }
}
