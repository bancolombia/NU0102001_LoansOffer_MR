package co.com.bancolombia.libreinversion.model.offer;

import co.com.bancolombia.libreinversion.model.account.Account;
import co.com.bancolombia.libreinversion.model.commons.Header;
import co.com.bancolombia.libreinversion.model.customer.CustomerData;
import co.com.bancolombia.libreinversion.model.customer.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RetrieveOfferData {
    private Header header;
    private Customer customer;
    private CustomerData customerData;
    private List<Account> depositAccounts;

    private List<OffersOperation> offers;

}
