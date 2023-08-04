package co.com.bancolombia.libreinversion.model.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class InfoDocument {
    private String title;
    private String author;
    private String subject;
    private String keywords;
    private String creator;
    private String nameFile;
    private byte[] byteArray;
}
