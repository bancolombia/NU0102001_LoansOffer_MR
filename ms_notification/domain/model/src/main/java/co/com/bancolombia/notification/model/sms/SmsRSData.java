package co.com.bancolombia.notification.model.sms;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class SmsRSData {

    String messageId;
    Integer totalSendedAlerts;
}
