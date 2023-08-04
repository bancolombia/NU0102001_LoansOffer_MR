package co.com.bancolombia.notification.model.email.attachment;

import co.com.bancolombia.notification.model.email.Parameter;
import co.com.bancolombia.notification.model.email.SendEmail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class EmailAttachedRQ {
    private DataEmail data;


    public static EmailAttachedRQ formTest() {
        return EmailAttachedRQ.builder()
                .data(DataEmail.builder()
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
                        .attached(Arrays.asList(Attached.builder()
                                .attachmentName("archivo.pdf")
                                .attachedBase64("JVBERi0xLjcKCjEgMCBvYmogICUgZW50cnkgcG9pbnQKPDwKICAvVHlwZSAvQ2F0YWxvZwogIC" +
                                        "9QYWdlcyAyIDAgUgo+PgplbmRvYmoKCjIgMCBvYmoKPDwKICAvVHlwZSAvUGFnZXMKICAvTWVkaWFCb3ggWy" +
                                        "AwIDAgMjAwIDIwMCBdCiAgL0NvdW50IDEKICAvS2lkcyBbIDMgMCBSIF0KPj4KZW5kb2JqCgozIDAgb2JqCjw" +
                                        "8CiAgL1R5cGUgL1BhZ2UKICAvUGFyZW50IDIgMCBSCiAgL1Jlc291cmNlcyA8PAogICAgL0ZvbnQgPDwKICAg" +
                                        "ICAgL0YxIDQgMCBSIAogICAgPj4KICA+PgogIC9Db250ZW50cyA1IDAgUgo+PgplbmRvYmoKCjQgMCBvYmoKP" +
                                        "DwKICAvVHlwZSAvRm9udAogIC9TdWJ0eXBlIC9UeXBlMQogIC9CYXNlRm9udCAvVGltZXMtUm9tYW4KPj4KZW" +
                                        "5kb2JqCgo1IDAgb2JqICAlIHBhZ2UgY29udGVudAo8PAogIC9MZW5ndGggNDQKPj4Kc3RyZWFtCkJUCjcwIDU" +
                                        "wIFRECi9GMSAxMiBUZgooSGVsbG8sIHdvcmxkISkgVGoKRVQKZW5kc3RyZWFtCmVuZG9iagoKeHJlZgowIDYK" +
                                        "MDAwMDAwMDAwMCA2NTUzNSBmIAowMDAwMDAwMDEwIDAwMDAwIG4gCjAwMDAwMDAwNzkgMDAwMDAgbiAKMDAwM" +
                                        "DAwMDE3MyAwMDAwMCBuIAowMDAwMDAwMzAxIDAwMDAwIG4gCjAwMDAwMDAzODAgMDAwMDAgbiAKdHJhaWxlcgo" +
                                        "8PAogIC9TaXplIDYKICAvUm9vdCAxIDAgUgo+PgpzdGFydHhyZWYKNDkyCiUlRU9G").build()))
                        .build()).build();
    }
}
