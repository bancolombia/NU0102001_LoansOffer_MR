package co.com.bancolombia.libreinversion.model.contactability.mail.emailsms;

import co.com.bancolombia.libreinversion.model.contactability.mail.Attached;
import co.com.bancolombia.libreinversion.model.contactability.mail.SendEmail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DataMailAttachedApiRQ {
    private String senderMail;
    private String subjectEmail;
    private String messageTemplateId;
    private String messageTemplateType;
    private List<SendEmail> sendEmail;
    private List<Attached> attached;
}
