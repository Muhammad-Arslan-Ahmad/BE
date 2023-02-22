package com.elastic.payload;

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
public class CreateUpdateUserResponse extends ServiceResponse implements Serializable {

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

    public CreateUpdateUserResponse(boolean status, String message) {
        super(status, message);
    }

    public CreateUpdateUserResponse(String id, String userName, String password, byte[] photo, String contentType, String scheduler, String userSenderOrganization, String userFirstName, String userLastName, String userPhone, Date createdAt, String status) {
        super();
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.photo = photo;
        this.contentType = contentType;
        this.scheduler = scheduler;
        this.userSenderOrganization = userSenderOrganization;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userPhone = userPhone;
        this.createdAt = createdAt;
        this.status = status;
    }
}
