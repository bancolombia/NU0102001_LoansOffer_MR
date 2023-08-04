package co.com.bancolombia.libreinversion.model.contactability.mail.emailsms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class BodyMailAttachedApiRQ {
    private DataMailAttachedApiRQ data;

}
