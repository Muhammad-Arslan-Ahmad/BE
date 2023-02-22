package com.elastic.payload;

import com.elastic.model.Narration;
import com.elastic.model.Product;
import com.elastic.security.payload.ServiceResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUpdateProductResponse extends ServiceResponse implements Serializable {

    String id;
    String name;
    byte[] attachment;
    String fileName;
    String contentType;
    List<String> productFeatures;
    boolean productFeatureAltitude;
    String productNameSelected;
    String productCategorySelected;
    List<String> productBenefits;
    boolean productBenefitAdvantage;
    List<String> productCapabilities;
    boolean productCapabilityHighlight;
    List<String> productRisks;
    List<String> proofNames;
    Date createdAt;
    String status;
    List<Narration> narrations;

    public CreateUpdateProductResponse(boolean status, String message) {
        super(status, message);
    }

    public CreateUpdateProductResponse(Product product) {
        super();
        if (product != null) {
            this.id = product.getId();
            this.name = product.getName();
            this.attachment = product.getAttachment();
            this.fileName = product.getFileName();
            this.contentType = product.getContentType();
            this.productFeatures = product.getProductFeatures();
            this.productFeatureAltitude = product.isProductFeatureAltitude();
            this.productNameSelected = product.getProductNameSelected();
            this.productCategorySelected = product.getProductCategorySelected();
            this.productBenefits = product.getProductBenefits();
            this.productBenefitAdvantage = product.isProductBenefitAdvantage();
            this.productCapabilities = product.getProductCapabilities();
            this.productCapabilityHighlight = product.isProductCapabilityHighlight();
            this.productRisks = product.getProductRisks();
            this.proofNames = product.getProofNames();
            this.createdAt = product.getCreatedAt();
            this.status = product.getStatus();
            this.narrations = product.getNarrations();
        }
    }
}
