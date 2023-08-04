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
public class AccountResponse {

    private String type;
    private String number;
    private boolean allowDebit;
    private boolean allowCredit;
    private String currency;
    private String inactiveDays;
    private String openingDate;
    private boolean jointHolder;
    private String overdraftDays;
    private String overdueDays;
    private String daysTerm;
    private Regime regime;
    private Balance balances;
    private List<Property> specifications;
    private Office office;
}
