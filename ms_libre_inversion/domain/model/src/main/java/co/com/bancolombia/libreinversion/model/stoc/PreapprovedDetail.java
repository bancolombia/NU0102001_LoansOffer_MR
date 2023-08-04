package co.com.bancolombia.libreinversion.model.stoc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PreapprovedDetail {

    private String expirationDate;
    private Integer capacity;
    private Integer minimumTerm;
    private Integer maximumTerm;
}
