package com.elastic.payload;

import com.elastic.model.Narration;
import com.elastic.model.Role;
import com.elastic.security.payload.ServiceResponse;
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
public class GetRoleResponse extends ServiceResponse implements Serializable {

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

    public GetRoleResponse(boolean status, String message) {
        super(status, message);
    }

    public GetRoleResponse(Role role) {
        if(role != null) {
            this.id = role.getId();
            this.roleName = role.getRoleName();
            this.jobTitle = role.getJobTitle();
            this.roleAltitudeLevel = role.getRoleAltitudeLevel();
            this.reportingRole = role.getReportingRole();
            this.attachment = role.getAttachment();
            this.fileName = role.getFileName();
            this.contentType = role.getContentType();
            this.expectations = role.getExpectations();
            this.painAndGains = role.getPainAndGains();
            this.possibilities = role.getPossibilities();
            this.impactedWorks = role.getImpactedWorks();
            this.hiddenPitfalls = role.getHiddenPitfalls();
            this.fileName = role.getFileName();
            this.createdAt = role.getCreatedAt();
            this.narrations = role.getNarrations();
        }
    }
}
