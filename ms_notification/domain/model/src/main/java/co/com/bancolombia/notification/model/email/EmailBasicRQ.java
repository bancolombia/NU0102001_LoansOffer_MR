package co.com.bancolombia.notification.model.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class EmailBasicRQ {

    List<EmailBasicData> data;

    public static EmailBasicRQ formTest() {

        return EmailBasicRQ.builder()
                .data(Arrays.asList(
                        EmailBasicData.builder()
                                .senderMail("Notificación <no-reply@bancolombia.com.co>")
                                .subjectEmail("Solicutud Libre Inversión")
                                .messageTemplateId("123456")
                                .messageTemplateType("masiv-template/html")
                                .sendEmail(Arrays.asList(SendEmail.builder()
                                        .destinationEmail("preuba@bancolombia.com.co")
                                        .parameter(Arrays.asList(Parameter.builder()
                                                .parameterName("Nombre")
                                                .parameterType("text")
                                                .parameterValue("prueba").build())).build()))
                                .build()
                )).build();
    }

}
