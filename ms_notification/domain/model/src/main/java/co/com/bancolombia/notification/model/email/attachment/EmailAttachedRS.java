package co.com.bancolombia.notification.model.email.attachment;

import co.com.bancolombia.notification.model.email.EmailBasicRSData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class EmailAttachedRS {
    private List<EmailBasicRSData> data;
}
