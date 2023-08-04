package co.com.bancolombia.libreinversion.model.customer;

import co.com.bancolombia.libreinversion.model.commons.TextLegalInformation;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerContact;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerDetail;
import co.com.bancolombia.libreinversion.model.notification.rest.ResponseRetrieveInfo;
import co.com.bancolombia.libreinversion.model.product.Insurance;
import co.com.bancolombia.libreinversion.model.product.Range;
import co.com.bancolombia.libreinversion.model.stoc.GeneralInformation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ValidateCustomer {

    private ResponseCustomerDetail detail;
    private ResponseCustomerContact contact;
    private ResponseRetrieveInfo alerts;
    private GeneralInformation stoc;
    private Customer customer;
    private String msgId;
    private Integer productCode;
    private Integer age;
    private String insuranceType;
    private String interestRateType;
    private String factor;
    private Integer minAmount;
    private Integer maxAmount;
    private String rateTypes;
    private Integer paymentDay;
    private Integer gracePeriod;

    private List<Range> rates;
    private Long cacheTime;

    private List<TextLegalInformation> texts;

    private Insurance insurance;
}
