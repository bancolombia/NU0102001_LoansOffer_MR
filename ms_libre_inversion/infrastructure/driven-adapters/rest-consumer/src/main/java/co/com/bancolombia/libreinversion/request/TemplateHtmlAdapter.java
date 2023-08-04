package co.com.bancolombia.libreinversion.request;


import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.IdTypeEnum;
import co.com.bancolombia.libreinversion.model.enums.InsuranceTypeEnum;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.response.DisbursementRS;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferComplete;
import co.com.bancolombia.libreinversion.model.request.ConfirmFactory;
import co.com.bancolombia.libreinversion.model.request.HtmlDocumentFactory;
import co.com.bancolombia.libreinversion.model.request.Insurances;
import co.com.bancolombia.libreinversion.model.request.Destination;
import co.com.bancolombia.libreinversion.model.request.gateways.TemplateHtmlGateways;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Component
public class TemplateHtmlAdapter implements TemplateHtmlGateways {


    @Override
    public Mono<String> setDataToPay(String html, Map<String,
            Object> utilLoad, ConfirmOfferComplete confirmOfferComplete) {
        return Mono.just(html)
                .flatMap(in -> {
                    String htmlWithData = in.replace("{}",
                            confirmOfferComplete.getResponseCustomerCommercial().getData().getFullName())
                            .replace("{}", confirmOfferComplete
                                    .getConfirmOfferRQ().getCustomer().getIdentification().getNumber());
                    return Mono.just(htmlWithData);
                });
    }

    @Override
    public Mono<String> setDataInstrutionLetter(String html, Map<String, Object> utilLoad,
                                                ConfirmOfferComplete confirmOfferComplete) {

        return Mono.just(html)
                .flatMap(in -> {
                    String htmlWithData = in.replace("{customer}",
                            confirmOfferComplete.getResponseCustomerCommercial().getData().getFullName())
                            .replace("{nitCustomer}", confirmOfferComplete
                                    .getConfirmOfferRQ().getCustomer().getIdentification().getNumber());
                    return Mono.just(htmlWithData);
                });
    }

    @Override
    public Mono<String> setDataOperationAnnex(String html, Map<String, Object> utilLoad,
                                              ConfirmOfferComplete confirmOfferComplete) {

        return Mono.just(html)
                .flatMap(in -> {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                    LocalDateTime now = LocalDateTime.now();
                    String htmlWithData = "";

                    String[] debict = ConfirmFactory.getAutomaticDebitAccount(confirmOfferComplete);
                    Destination destinationAwner = ConfirmFactory.getAccountOwner(confirmOfferComplete);
                    String plan = getFirstValue(confirmOfferComplete);

                    htmlWithData = in.replace("{}", dtf.format(now))
                            .replace("{}",
                                    confirmOfferComplete.getResponseCustomerCommercial().getData().getFullName())
                            .replace("{}", dtf.format(now));

                    return Mono.just(htmlWithData);
                });
    }

    private String getFirstValue(ConfirmOfferComplete confirmOfferComplete) {
        String typeInsurance = confirmOfferComplete.getConfirmOfferRQ().getInsurances().get(0).getType();
        Object firstKey = confirmOfferComplete.getRuleResponse().getData().getUtilLoad()
                .get(Constant.PLAN + "" + typeInsurance);
        return firstKey.toString();
    }

    public Mono<String> setDataEmploymentInsuraceLetter(String htmlDoc, ConfirmOfferComplete confirmOfferComp,
                                                        String codCardif, DisbursementRS disbursementRS) {
        return Mono.just(htmlDoc).flatMap(html -> {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d MMM u");
            LocalDate now = LocalDate.now();
            String htmlWithData = html
                    .replace("{}", disbursementRS.getData().getLoanId());
            return Mono.just(htmlWithData);
        });
    }

    public Mono<String> setDataTemplateWeelcome(String htmlDoc, ConfirmOfferComplete confirmOfferComp) {
        return Mono.just(htmlDoc).flatMap(html -> {
            final Optional<Insurances> employment = confirmOfferComp.getConfirmOfferRQ().getInsurances()
                    .stream().filter(insurance -> insurance.getType()
                            .equals(InsuranceTypeEnum.SD.getKey())).findFirst();

            String[] debict = ConfirmFactory.getAutomaticDebitAccount(confirmOfferComp);
            html = html
                    .replace("$(nombre)", confirmOfferComp.getResponseCustomerCommercial()
                            .getData().getFullName())
                    .replace("$(visible)", employment.isPresent() ? "" : "none");
            return Mono.just(html);
        });
    }
}
