package co.com.bancolombia.notification.model.email.attachment;

import co.com.bancolombia.notification.model.email.SendEmail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DataEmail {
    private String senderMail;
    private String subjectEmail;
    private String messageTemplateId;
    private String messageTemplateType;
    private List<SendEmail> sendEmail;
    private List<Attached> attached;
}
