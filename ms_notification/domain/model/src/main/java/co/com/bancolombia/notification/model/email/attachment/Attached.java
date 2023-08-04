package co.com.bancolombia.notification.model.email.attachment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Attached {
    private String attachedBase64;
    private String attachmentName;
}
