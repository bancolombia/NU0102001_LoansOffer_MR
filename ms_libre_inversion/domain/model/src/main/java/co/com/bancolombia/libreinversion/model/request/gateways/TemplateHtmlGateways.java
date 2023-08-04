package co.com.bancolombia.libreinversion.model.request.gateways;

import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.response.DisbursementRS;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferComplete;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface TemplateHtmlGateways {

    Mono<String> setDataToPay(String html, Map<String, Object> utilLoad, ConfirmOfferComplete confirmOfferComplete);

    Mono<String> setDataInstrutionLetter(String html, Map<String, Object> utilLoad,
                                         ConfirmOfferComplete confirmOfferComplete);

    Mono<String> setDataOperationAnnex(String html, Map<String, Object> utilLoad,
                                       ConfirmOfferComplete confirmOfferComplete);

    Mono<String> setDataEmploymentInsuraceLetter(String html, ConfirmOfferComplete confirmOfferComplete,
                                                 String codCardif, DisbursementRS disbursementRS);

    Mono<String> setDataTemplateWeelcome(String html, ConfirmOfferComplete confirmOfferComplete);
}
