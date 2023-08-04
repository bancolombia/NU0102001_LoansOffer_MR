package co.com.bancolombia.libreinversion.model.events;

import co.com.bancolombia.libreinversion.model.contactability.mail.emailsms.DataMailBasicApiRQ;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class QueueMessageEmail {
    private String type;
    private String messageId;
    private String priority;
    private DataMailBasicApiRQ data;
}
