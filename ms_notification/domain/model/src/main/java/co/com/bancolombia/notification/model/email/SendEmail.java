package co.com.bancolombia.notification.model.email;


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

    String destinationEmail;
    List<Parameter> parameter;

}
