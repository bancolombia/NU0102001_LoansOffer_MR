package co.com.bancolombia.notification.model.sms;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class SmsMessage {

    String priorityId;
    String transactionId;
    String emergingMessageId;
    List<Integer> phoneNumbers;
    String textMessage;
}
