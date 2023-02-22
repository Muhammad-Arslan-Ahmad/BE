package com.elastic.payload;

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
public class ProofRequest implements Serializable {

    String id;
    String proofName;
    String proofOrganization;
    String proofImpact;
    String productAlignment;
    String proofType;
    String proofTitle;
    String proofAltitude;
    String proofContent;
    String proofLink;
    String proofReferenceableAccount;
    List<String> customerStories;
    List<String> customerQuotes;
}
