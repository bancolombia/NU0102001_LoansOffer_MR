package co.com.bancolombia.libreinversion.model.account.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Balance {

    private Integer available;
    private Integer availableOverdraftBalance;
    private Integer current;
}
