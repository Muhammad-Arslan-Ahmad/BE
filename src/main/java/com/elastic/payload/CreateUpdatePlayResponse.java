package com.elastic.payload;

import com.elastic.model.Narration;
import com.elastic.model.Play;
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
public class CreateUpdatePlayResponse extends ServiceResponse implements Serializable {

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

    public CreateUpdatePlayResponse(boolean status, String message) {
        super(status, message);
    }

    public CreateUpdatePlayResponse(Play play) {
        if(play != null) {
            this.id = play.getId();
            this.playName = play.getPlayName();
            this.attachment = play.getAttachment();
            this.fileName = play.getFileName();
            this.contentType = play.getContentType();
            this.playBusinessOutcome = play.getPlayBusinessOutcome();
            this.playIndustry = play.getPlayIndustry();
            this.playSolutionType = play.getPlaySolutionType();
            this.playProductCategory = play.getPlayProductCategory();
            this.playProductNames = play.getPlayProductNames();
            this.playRoles = play.getPlayRoles();
            this.playStart = play.getPlayStart();
            this.playEnd = play.getPlayEnd();
            this.playWinThemes = play.getPlayWinThemes();
            this.createdAt = play.getCreatedAt();
            this.status = play.getStatus();
            this.narrations = play.getNarrations();
        }
    }
}
