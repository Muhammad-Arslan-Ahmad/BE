package com.elastic.payload;

import com.elastic.model.Narration;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUpdateRoleRequest implements Serializable {

    String id;
    String roleName;
    String jobTitle;
    String roleAltitudeLevel;
    String reportingRole;
    byte[] attachment;
    String fileName;
    String contentType;
    List<String> expectations;
    List<String> painAndGains;
    List<String> possibilities;
    List<String> impactedWorks;
    List<String> hiddenPitfalls;
    List<Narration> narrations;
    Date createdAt;
    String status;
}
