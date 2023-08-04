package co.com.bancolombia.libreinversion.model.request;


import co.com.bancolombia.libreinversion.model.account.rest.DepositAccountResponse;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerBasic;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerCommercial;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerContact;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerDetail;
import co.com.bancolombia.libreinversion.model.notification.rest.ResponseRetrieveInfo;
import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import co.com.bancolombia.libreinversion.model.rate.RangeType;
import co.com.bancolombia.libreinversion.model.stoc.GeneralInformation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ConfirmOfferComplete {

    private ResponseCustomerDetail responseCustomerDetail;
    private ResponseCustomerContact responseCustomerContact;
    private ResponseCustomerBasic responseCustomerBasic;
    private ResponseCustomerCommercial responseCustomerCommercial;
    private DepositAccountResponse depositAccountResponse;
    private ResponseRetrieveInfo responseRetrieveInfo;
    private GeneralInformation generalInformation;

    private ConfirmOfferRQ confirmOfferRQ;
    private RuleResponse ruleResponse;

    private RangeType rangeType;
}
