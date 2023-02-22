package com.elastic.model;

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
public class Play implements Serializable {

    String id;
    String playName;
    byte[] attachment;
    String fileName;
    String contentType;
    String playBusinessOutcome;
    List<String> playIndustry;
    String playSolutionType;
    String playProductCategory;
    List<String> playProductNames;
    List<String> playRoles;
    Date playStart;
    Date playEnd;
    List<String> playWinThemes;
    Date createdAt;
    String status;
    String playStartStr;
    String playEndStr;
    List<Narration> narrations;
}
