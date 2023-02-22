package com.elastic.payload;

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
public class CreateUpdateUserRequest implements Serializable {

    String id;
    String userName;
    String password;
    byte[] photo;
    String fileName;
    String contentType;
    String scheduler;
    String userSenderOrganization;
    String userFirstName;
    String userLastName;
    String userPhone;
    Date createdAt;
    String status;
}
