package co.com.bancolombia.libreinversion.model.offer.selltestutil;

import co.com.bancolombia.libreinversion.model.account.rest.Identification;
import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.DocumentUtil;
import co.com.bancolombia.libreinversion.model.constants.SellConst;
import co.com.bancolombia.libreinversion.model.customer.Customer;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.Beneficiary;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.DepositAccount;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.DirectDebit;
import co.com.bancolombia.libreinversion.model.offer.Loan;
import co.com.bancolombia.libreinversion.model.rate.RangeType;
import co.com.bancolombia.libreinversion.model.request.Destination;
import co.com.bancolombia.libreinversion.model.request.DisbursementDestination;
import co.com.bancolombia.libreinversion.model.request.SellOfferRQ;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

public class UtilTestBase {

    protected static final String TYPE_DOC = "pruebas";
    protected static final String MSG_ID = "123456";
    protected static final String SEND_MAIL = "Notificación <pruebas>";
    protected static final String REQ_LIBRE_INV = "Solicutud Libre Inversión";
    protected static final String MSG_TEMPLATE = "masiv-template/html";
    protected static final String MAIL_TEST = "pruebas";
    protected static final String NAME = "Nombre";
    protected static final String ID_TEST = "1234567";
    protected static final Integer VAL_TWO_TEST = 2;
    protected static final Integer VAL_ONE_TEST = 1;
    protected static final int VAL_FIVE_TEST = 5;
    protected static final BigDecimal RATE_TEST = BigDecimal.valueOf(0.152416);
    protected static final String TYPE = "pruebas";
    protected static final String DESTINATION_ID = "pruebas";
    protected static final String AMOUNT = "1500000";
    protected static final BigDecimal MAX_AMOUNT = new BigDecimal("5999999");
    protected static final BigDecimal MIN_AMOUNT = new BigDecimal("3000000");
    protected static final TechLogger log = LoggerFactory.getLog(DocumentUtil.class.getName());
    protected static Constant constant;
    protected static Map<String, Object> utilLoad;
    protected static final String NUMBER_ACCOUNT = "123456789";

    public static Identification getIdentification() {
        return Identification.builder().type("pruebas").build();
    }

    public static SellOfferRQ buildSellOfferRQ() {
        return SellOfferRQ.builder().legalTrace("legal trace pruebas")
                .customer(Customer.builder()
                        .identification(getIdentification())

                        .build())
                .loans(Arrays.asList(Loan.builder()
                        .signingCode(NUMBER_ACCOUNT)
                        .directDebit(DirectDebit.builder()
                                .allowDirectDebit(SellConst.YES)
                                .depositAccount(DepositAccount.builder()
                                        .type(TYPE)
                                        .number(NUMBER_ACCOUNT).build()).build())
                        .disbursementDestination(DisbursementDestination.builder()
                                .destination(Arrays.asList(buildDestination(), buildDestination()))
                                .build())
                        .referralOfficer("12345").advisoryCode("12345")
                        .promotionalCode("pruebas")
                        .paymentDay(VAL_TWO_TEST)
                        .branchOffice("12345678").build()))
                .alternativeCellPhoneNumber("pruebas").build();
    }

    public static RangeType buildRangeType() {
        final BigDecimal monthly = BigDecimal.valueOf(12);
        final int term = 60;
        return RangeType.builder().rateType("F")
                .maximumAmount(MAX_AMOUNT)
                .minimumAmount(MIN_AMOUNT)
                .maximumTerm(term)
                .minimumTerm(term)
                .monthlyRate(monthly)
                .annualNominalMonthlyRate(RATE_TEST)
                .effectiveAnnualRate(RATE_TEST)
                .fixedTermDepositRate(RATE_TEST)
                .fixedTermDepositScore(RATE_TEST)
                .arrearsRate(RATE_TEST).build();
    }

    protected UtilTestBase() {
        throw new IllegalStateException("Utility class");
    }


    public static Beneficiary buidBeneficiary() {
        return Beneficiary.builder()
                .fullName("pruebas")
                .identification(buildIdentification()).build();
    }

    public static Identification buildIdentification() {
        return Identification.builder()
                .type("pruebas")
                .number(ID_TEST).build();
    }

    public static Destination buildDestination() {
        return Destination.builder()
                .destinationType(TYPE)
                .destinationId(DESTINATION_ID)
                .amount(Double.valueOf(AMOUNT))
                .beneficiary(buidBeneficiary())
                .build();
    }
}
