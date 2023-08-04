package co.com.bancolombia.notification.model.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class QueueMessage {

    private String type;
    private String messageId;
    private String priority;
    private Object data;
}
