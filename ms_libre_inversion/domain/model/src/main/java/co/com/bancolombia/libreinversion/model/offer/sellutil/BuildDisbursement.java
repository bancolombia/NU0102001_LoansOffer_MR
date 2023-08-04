package co.com.bancolombia.libreinversion.model.offer.sellutil;

import co.com.bancolombia.libreinversion.model.account.rest.Identification;
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
import co.com.bancolombia.libreinversion.model.offer.Loan;
import co.com.bancolombia.libreinversion.model.rate.RangeType;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferComplete;
import co.com.bancolombia.libreinversion.model.request.Destination;
import co.com.bancolombia.libreinversion.model.request.Insurances;
import co.com.bancolombia.libreinversion.model.request.SellOfferRQ;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BuildDisbursement extends SellBuildBase {

    private BuildDisbursement() {
        throw new IllegalStateException("Utility class");
    }

    public static Optional<DisbursementRQ> buildDisbursementRQ(ConfirmOfferComplete confirmOfferCompl,
                                                               SellOfferRQ sellOfferRQ, RangeType rangeType,
                                                               String creditPlan) {
        final int maxId = 9;
        try {
            LocalTime time = LocalTime.now();
            String disbursementId = "SELL_" + strDate("ddMMyyyy") + time.getSecond();
            String trackingId = sellOfferRQ.getCustomer().getIdentification().getNumber().substring(0, maxId);
            LoanDM loanDM = buildLoanDM(confirmOfferCompl, sellOfferRQ, rangeType, creditPlan);

            DisbursementRQ res = DisbursementRQ.builder()
                    .data(DisburmentsData.builder()
                            .trackingId(trackingId)
                            .disbursementAppilicationId(disbursementId)
                            .loan(loanDM)
                            .disbursementDestination(DisbursementDestinationDbm.builder()
                                    .destination(buildDestination(confirmOfferCompl, sellOfferRQ)).build())
                            .build()).build();
            return Optional.of(res);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Optional.empty();
    }

    public static DirectDebit buildDirectDebit(SellOfferRQ sellOfferRQ) {
        DirectDebit directDebit = DirectDebit.builder().allowDirectDebit(sellConst.NOT).build();
        DepositAccount depAccount = DepositAccount.builder().build();
        Loan loanSellRQ = sellOfferRQ.getLoans().stream().findFirst().orElse(null);

        if (loanSellRQ != null && loanSellRQ.getDirectDebit().getAllowDirectDebit().equals(SellConst.YES)) {
            directDebit = loanSellRQ.getDirectDebit();
        }
        directDebit.setDepositAccount(depAccount);
        return directDebit;
    }

    private static List<InsuranceDM> findInsurances(ConfirmOfferComplete confirmOfferCompl) {
        List<InsuranceDM> insurances = null;
        if (confirmOfferCompl.getConfirmOfferRQ().getInsurances() != null) {
            insurances = new ArrayList<>();
            for (Insurances InsuranceConfirm : confirmOfferCompl.getConfirmOfferRQ().getInsurances()) {
                InsuranceDM insuranceDM = InsuranceDM.builder()
                        .type(InsuranceConfirm.getType())
                        //.rate(InsuranceConfirm.getFactor().doubleValue())
                        .build();
                insurances.add(insuranceDM);
            }
        }
        return insurances;
    }

    public static LoanDM buildLoanDM(ConfirmOfferComplete confirmCompl, SellOfferRQ sellOfferRQ,
                                     RangeType rangeType, String creditPlan) {
        DirectDebit directDebit = buildDirectDebit(sellOfferRQ);
        List<InsuranceDM> insurances = findInsurances(confirmCompl);
        Loan loanSellRQ = sellOfferRQ.getLoans().stream().findFirst().orElse(Loan.builder().build());

        return LoanDM.builder()
                .customer(Customer.builder().identification(Identification.builder()
                        .type(sellOfferRQ.getCustomer().getIdentification().getType())
                        .number(sellOfferRQ.getCustomer().getIdentification().getNumber())
                        .build()).build())
                .creditLimitId("1")
                .creditPlan(creditPlan)
                .amount(Double.parseDouble(confirmCompl.getConfirmOfferRQ().getOffer().getAmount()))
                .term(Integer.parseInt(confirmCompl.getConfirmOfferRQ().getOffer().getTerm()))
                .interestRate(rangeType.getArrearsRate().doubleValue())
                .paymentFrequency(1)
                .paymentDay(loanSellRQ == null ? null : loanSellRQ.getPaymentDay())
                .backedByCollateral(sellConst.NOT)
                .directDebit(directDebit)
                .insurances(insurances)
                .approverOfficer(loanSellRQ != null ? loanSellRQ.getBranchOffice() : null)
                .branchOffice(loanSellRQ != null ? loanSellRQ.getBranchOffice() : null)
                .build();
    }

    public static List<DestinationDbm> buildDestination(ConfirmOfferComplete confirmCompl, SellOfferRQ sellOfferRQ) {
        List<DestinationDbm> destinations = new ArrayList<>();
        try {
            for (Loan loanSellRQ : sellOfferRQ.getLoans()) {
                for (Destination destLoan : loanSellRQ.getDisbursementDestination().getDestination()) {
                    DestinationDbm destination = DestinationDbm.builder()
                            .destinationType(destLoan.getDestinationType())
                            .destinationId(destLoan.getDestinationId())
                            .amount(destLoan.getAmount())
                            .transactionTaxes(sellConst.NOT)
                            .office(loanSellRQ.getBranchOffice())
                            .beneficiary(destLoan.getBeneficiary()).build();

                    destinations.add(destination);
                }
                break;
            }
            return destinations;
        } catch (Exception e) {
            log.error("buildDestination: " + e);
        }
        return null;
    }

}
