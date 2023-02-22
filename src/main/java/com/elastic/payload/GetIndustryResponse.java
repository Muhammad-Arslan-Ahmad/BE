package com.elastic.payload;

import com.elastic.model.Industry;
import com.elastic.model.Narration;
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
public class GetIndustryResponse extends ServiceResponse implements Serializable {
    String id;
    String name;
    byte[] attachment;
    String fileName;
    String contentType;
    String industryExternalForce;
    boolean industryExternalForceEmphasize;
    List<String> industryExternalForceImpacts;
    String industrySelected;
    List<String> industryTrends;
    String industryRoleSelected;
    boolean IndustryTrendPrioritize;
    Date createdAt;
    String status;
    List<Narration> narrations;

    public GetIndustryResponse(boolean status, String message) {
        super(status, message);
    }

    public GetIndustryResponse(Industry industry) {
        if(industry != null) {
            this.id = industry.getId();
            this.name = industry.getName();
            this.attachment = industry.getAttachment();
            this.fileName = industry.getFileName();
            this.contentType = industry.getContentType();
            this.industryExternalForce = industry.getIndustryExternalForce();
            this.industryExternalForceEmphasize = industry.isIndustryExternalForceEmphasize();
            this.industryExternalForceImpacts = industry.getIndustryExternalForceImpacts();
            this.industrySelected = industry.getIndustrySelected();
            this.industryTrends = industry.getIndustryTrends();
            this.industryRoleSelected = industry.getIndustryRoleSelected();
            this.IndustryTrendPrioritize = industry.isIndustryTrendPrioritize();
            this.createdAt = industry.getCreatedAt();
            this.status = industry.getStatus();
            this.narrations = industry.getNarrations();
        }
    }
}
