package co.com.bancolombia.libreinversion.model.loanamount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class LoanAmountTestUtil {

    public static RequestLoanAmount request() {
        return RequestLoanAmount.builder()
                .data(RequestLoanAmountData.builder()
                        .customer(Customer.builder()
                                .customerIdNumber("2101067990")
                                .customerId("FS001")
                                .build())
                        .build())
                .build();
    }

    public static SuccessResponseLoanAmount response() {
        List<Quota> quotaList = new ArrayList<>();
        quotaList.add(Quota.builder()
                .sequenceNumber(new BigDecimal(1))
                .copApprovedAmount(new BigDecimal("40000000")).lineOfCreditCode("OPE").build());
        quotaList.add(Quota.builder()
                .sequenceNumber(new BigDecimal(1))
                .copApprovedAmount(new BigDecimal("30000000")).lineOfCreditCode("OPE").build());
        quotaList.add(Quota.builder()
                .sequenceNumber(new BigDecimal(1))
                .copApprovedAmount(new BigDecimal("20000000")).lineOfCreditCode("OPE").build());
        quotaList.add(Quota.builder()
                .sequenceNumber(new BigDecimal(1))
                .copApprovedAmount(new BigDecimal("50000000")).lineOfCreditCode("OPE").build());
        quotaList.add(Quota.builder()
                .sequenceNumber(new BigDecimal(1))
                .copApprovedAmount(new BigDecimal("60000000")).lineOfCreditCode("OPE").build());
        quotaList.add(Quota.builder()
                .sequenceNumber(new BigDecimal(1))
                .copApprovedAmount(new BigDecimal("70000000")).lineOfCreditCode("OPE").build());
        return SuccessResponseLoanAmount.builder()
                .data(SuccessResponseLoanAmountData.builder().quota(quotaList).build()).build();
    }
}
