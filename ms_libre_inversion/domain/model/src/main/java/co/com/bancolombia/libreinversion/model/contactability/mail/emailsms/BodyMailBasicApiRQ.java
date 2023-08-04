package co.com.bancolombia.libreinversion.model.contactability.mail.emailsms;

import co.com.bancolombia.libreinversion.model.contactability.mail.MailParameter;
import co.com.bancolombia.libreinversion.model.contactability.mail.SendEmail;
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
public class BodyMailBasicApiRQ {

    private List<DataMailBasicApiRQ> data;


    public static BodyMailBasicApiRQ fromTest() {
        return BodyMailBasicApiRQ.builder().data(Arrays.asList(
                DataMailBasicApiRQ.builder()
                        .senderMail("Alertas y Notificaciones<alertas@solicitudesgrupobancolombia.com.co")
                        .subjectEmail("subject prueba")
                        .messageTemplateId("messageTemplateId preuba")
                        .messageTemplateType("messageTemplateType preuba")
                        .sendEmail(Arrays.asList(SendEmail.builder()
                                .destinationEmail("jlagos@bancolombia.com.co")
                                .parameter(Arrays.asList(
                                        MailParameter.builder()
                                                .parameterName("Name")
                                                .parameterType("type")
                                                .parameterValue("valor campo1")
                                                .build())
                                ).build())).build())).build();
    }
}
