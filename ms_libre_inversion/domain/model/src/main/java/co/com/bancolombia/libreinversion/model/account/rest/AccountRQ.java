package co.com.bancolombia.libreinversion.model.account.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AccountRQ {
    private boolean allowDebit;
    private boolean allowCredit;
    private Participant participant;
    private List<Property> specifications;
}
