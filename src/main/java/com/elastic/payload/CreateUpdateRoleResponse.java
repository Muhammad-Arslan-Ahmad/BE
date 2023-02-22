package com.elastic.payload;

import com.elastic.model.Narration;
import com.elastic.security.payload.ServiceResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUpdateRoleResponse extends ServiceResponse {

    String id;
    String roleName;
    String jobTitle;
    String roleAltitudeLevel;
    String reportingRole;
    byte[] attachment;
    List<String> expectations;
    List<String> painAndGains;
    List<String> possibilities;
    List<String> impactedWorks;
    List<String> hiddenPitfalls;
    List<Narration> narrations;

    Date createdAt;

    public CreateUpdateRoleResponse(boolean status, String message) {
        super(status, message);
    }

    public CreateUpdateRoleResponse(String id,String roleName,String jobTitle,String roleAltitudeLevel,String reportingRole,byte[] attachment,List<String> expectations,List<String> painAndGains,List<String> possibilities,List<String> impactedWorks,List<String> hiddenPitfalls, Date createdAt, List<Narration> narrations) {
        super();

        this.id = id;
        this.roleName = roleName;
        this.jobTitle = jobTitle;
        this.roleAltitudeLevel = roleAltitudeLevel;
        this.reportingRole = reportingRole;
        this.attachment = attachment;
        this.expectations = expectations;
        this.painAndGains = painAndGains;
        this.possibilities = possibilities;
        this.impactedWorks = impactedWorks;
        this.hiddenPitfalls = hiddenPitfalls;
        this.createdAt = createdAt;
        this.narrations = narrations;
    }

}
