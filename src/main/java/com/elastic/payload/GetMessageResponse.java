package com.elastic.payload;


import com.elastic.model.Message;
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
public class GetMessageResponse extends ServiceResponse implements Serializable {

    String id;
    String name;
    byte[] attachment;
    String fileName;
    String contentType;
    String messageProductAlignment;
    String messageRoleAlignment;
    List<String> messageValuePoints;
    boolean messageValueHighlight;
    List<String> messageQuestions;
    boolean messageVeryImportant;
    String messageQuestionType;
    List<String> messageObjections;
    String messageResponseName;
    List<String> messageResponses;
    List<String> messageProblemResolutions;
    String messageWinThemeAlignment;
    String messageIndustryAlignment;
    boolean messageProblemCritical;
    Date createdAt;
    String status;
    List<Narration> narrations;

    public GetMessageResponse(boolean status, String message) {
        super(status, message);
    }

    public GetMessageResponse(Message object) {
        super();
        if (object != null) {
            this.id = object.getId();
            this.name = object.getName();
            this.attachment = object.getAttachment();
            this.fileName = object.getFileName();
            this.contentType  = object.getContentType();
            this.messageProductAlignment = object.getMessageProductAlignment();
            this.messageRoleAlignment = object.getMessageRoleAlignment();
            this.messageValuePoints = object.getMessageValuePoints();
            this.messageValueHighlight = object.isMessageValueHighlight();
            this.messageQuestions = object.getMessageQuestions();
            this.messageVeryImportant = object.isMessageVeryImportant();
            this.messageQuestionType = object.getMessageQuestionType();
            this.messageObjections = object.getMessageObjections();
            this.messageResponseName = object.getMessageResponseName();
            this.messageResponses = object.getMessageResponses();
            this.messageProblemResolutions = object.getMessageProblemResolutions();
            this.messageWinThemeAlignment = object.getMessageWinThemeAlignment();
            this.messageIndustryAlignment = object.getMessageIndustryAlignment();
            this.messageProblemCritical = object.isMessageProblemCritical();
            this.createdAt = object.getCreatedAt();
            this.status = object.getStatus();
            this.narrations = object.getNarrations();
        }
    }
}
