package co.com.bancolombia.libreinversion.model.notification.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CustomerIdentification {
    private String documentType;
    private String documentNumber;
}
