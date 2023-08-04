package co.com.bancolombia.libreinversion.model.contactability.mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class SendEmail {
    private String destinationEmail;
    private List<MailParameter> parameter;
}
