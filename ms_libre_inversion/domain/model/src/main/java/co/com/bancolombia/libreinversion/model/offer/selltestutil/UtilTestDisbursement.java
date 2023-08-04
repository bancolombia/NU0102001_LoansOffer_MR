package co.com.bancolombia.libreinversion.model.offer.selltestutil;

import co.com.bancolombia.libreinversion.model.constants.SellConst;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.Customer;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.DepositAccount;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.DestinationDbm;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.DirectDebit;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.DisburmentsData;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.DisbursementDestinationDbm;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.DisbursementRQ;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.InsuranceDM;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.LoanDM;

import java.util.Arrays;

public class UtilTestDisbursement extends UtilTestBase {

    private UtilTestDisbursement() {
        throw new IllegalStateException("Utility class");
    }

    public static DisbursementRQ buildDisbursementRQ() {
        return DisbursementRQ.builder()
                .data(DisburmentsData.builder()
                        .trackingId(ID_TEST)
                        .disbursementAppilicationId(ID_TEST)
                        .loan(buildLoanDM())
                        .disbursementDestination(DisbursementDestinationDbm.builder().destination(
                                        Arrays.asList(buildDestinationDbm()))
                                .build())
                        .build()).build();

    }

    public static LoanDM buildLoanDM() {
        return LoanDM.builder()
                .customer(Customer.builder()
                        .identification(buildIdentification()).build())
                .creditLimitId("0")
                .creditPlan("P59")
                .amount(Double.valueOf(AMOUNT))
                .term(VAL_FIVE_TEST)
                .interestRate(RATE_TEST.doubleValue())
                .paymentFrequency(VAL_ONE_TEST)
                .paymentDay(VAL_FIVE_TEST)
                .backedByCollateral(SellConst.NOT)
                .directDebit(buildDirectDebit())
                .insurances(Arrays.asList(InsuranceDM.builder()
                        .type("SD").type(RATE_TEST.toString()).build()))
                .approverOfficer(VAL_ONE_TEST.toString())
                .branchOffice(VAL_TWO_TEST.toString())
                .collateralId("0")
                .build();
    }

    public static DirectDebit buildDirectDebit() {
        return DirectDebit.builder()
                .depositAccount(DepositAccount.builder()
                        .number(DESTINATION_ID)
                        .type(TYPE).build())
                .allowDirectDebit(SellConst.YES).build();
    }


    public static DestinationDbm buildDestinationDbm() {
        return DestinationDbm.builder()
                .destinationType("AHO")
                .transactionTaxes(SellConst.NOT)
                .destinationId(DESTINATION_ID)
                .amount(Double.valueOf(AMOUNT))
                .office(VAL_ONE_TEST.toString())
                .beneficiary(buidBeneficiary())
                .build();
    }


}
