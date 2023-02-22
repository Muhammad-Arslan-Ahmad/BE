package com.elastic.payload;

import com.elastic.model.Narration;
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
public class CreateUpdateMessageResponse extends ServiceResponse implements Serializable {

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

    public CreateUpdateMessageResponse(boolean status, String message) {
        super(status, message);
    }
}
