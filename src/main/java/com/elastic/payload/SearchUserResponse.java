package com.elastic.payload;

import com.elastic.model.User;
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
public class SearchUserResponse extends ServiceResponse implements Serializable {

    List<User> users;
    Long totalCount;

    public SearchUserResponse(boolean status, String message) {
        super(status, message);
    }

    public SearchUserResponse(List<User> users, Long totalCount) {
        super();
        this.users = users;
        this.totalCount  = totalCount;
    }
}
