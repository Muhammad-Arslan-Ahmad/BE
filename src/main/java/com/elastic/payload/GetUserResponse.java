package com.elastic.payload;

import com.elastic.model.User;
import com.elastic.security.payload.ServiceResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetUserResponse extends ServiceResponse implements Serializable {

    String id;
    String userName;
    String password;
    byte[] photo;
    String contentType;
    String scheduler;
    String userSenderOrganization;
    String userFirstName;
    String userLastName;
    String userPhone;
    Date createdAt;
    String status;

    public GetUserResponse(boolean status, String message) {
        super(status, message);
    }

    public GetUserResponse(User object) {
        super();
        if(object != null) {
            this.id = object.getId();
            this.userName = object.getUserName();
            this.password = object.getPassword();
            this.photo = object.getPhoto();
            this.contentType = object.getContentType();
            this.scheduler = object.getScheduler();
            this.userSenderOrganization = object.getUserSenderOrganization();
            this.userFirstName = object.getUserFirstName();
            this.userLastName = object.getUserLastName();
            this.userPhone = object.getUserPhone();
            this.createdAt = object.getCreatedAt();
            this.status = object.getStatus();
        }
    }
}
