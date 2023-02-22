package com.elastic.payload;

import com.elastic.model.Narration;
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
public class CreateUpdateProductRequest implements Serializable {

    String id;
    String name;
    byte[] attachment;
    String fileName;
    String contentType;
    List<String> productFeatures;
    boolean productFeatureAltitude;
    String productNameSelected;
    String productCategorySelected;
    List<String> ProductBenefits;
    boolean productBenefitAdvantage;
    List<String> productCapabilities;
    boolean productCapabilityHighlight;
    List<String> productRisks;
    List<String> proofNames;
    Date createdAt;
    String status;
    List<Narration> narrations;
}
