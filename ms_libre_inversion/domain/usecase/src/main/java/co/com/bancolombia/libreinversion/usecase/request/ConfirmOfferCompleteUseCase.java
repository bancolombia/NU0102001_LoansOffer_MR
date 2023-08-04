package co.com.bancolombia.libreinversion.usecase.request;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerCommercial;
import co.com.bancolombia.libreinversion.model.document.Document;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;

import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import co.com.bancolombia.libreinversion.model.product.ResponseData;
import co.com.bancolombia.libreinversion.model.product.Attribute;
import co.com.bancolombia.libreinversion.model.product.RuleRequest;

import co.com.bancolombia.libreinversion.model.product.gateways.MAPGateways;
import co.com.bancolombia.libreinversion.model.rate.LoanInteresRateResponse;


import co.com.bancolombia.libreinversion.model.request.ConfirmOfferRQ;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferResponse;
import co.com.bancolombia.libreinversion.model.request.ConfirmFactory;
import co.com.bancolombia.libreinversion.model.request.HtmlDocumentFactory;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferComplete;


import co.com.bancolombia.libreinversion.model.request.gateways.AmazonS3Gateways;
import co.com.bancolombia.libreinversion.model.request.gateways.HtmlPdfGateways;
import co.com.bancolombia.libreinversion.model.request.gateways.RedisGateways;
import co.com.bancolombia.libreinversion.model.request.gateways.TemplateHtmlGateways;
import co.com.bancolombia.libreinversion.model.stoc.GeneralInformation;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RequiredArgsConstructor
public class ConfirmOfferCompleteUseCase {

    private final MAPGateways mapGateways;
    private final HtmlPdfGateways htmlPdfGateways;
    private final TemplateHtmlGateways templateHtmlGateways;
    private final AmazonS3Gateways amazonS3Gateways;
    private final RedisGateways redisGateways;

    public Mono<ConfirmOfferResponse> comfirmComplete(ConfirmOfferRQ confirmOfferRQ,
                                                      String messageId, String bucketName) {

        return runRuleAvailability()
                .filter(r -> r.getData().isValid())
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new LibreInversionException(ErrorEnum.MSG_LI016.getName(),
                                ErrorEnum.MSG_LI016.getName(), ErrorEnum.MSG_LI016.getMessage(), "", "", messageId))))
                .flatMap(ben -> ConfirmFactory.validateBeneficiary())
                .filter(ben -> ben)
                .flatMap(confirm -> HtmlDocumentFactory.validateAmmount(confirmOfferRQ, messageId))
                .flatMap(param ->
                        runRule(
                                ConfirmFactory.getAtributes(Constant.OFFER_TIME_PARAM_NAME,
                                        Constant.RULE_FREE_INVESTMENT_VALUE),
                                Constant.RULE_FREE_INVESTMENT_DOC,
                                Constant.RULE_FREE_INVESTMENT_VALUE)
                )
                .filter(param -> param.getData().isValid())
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new LibreInversionException(ErrorEnum.MSG_VP006.getName(),
                                ErrorEnum.MSG_VP006.getName(), ErrorEnum.MSG_VP006.getMessage(), "", "", messageId))))
                .flatMap(param -> getTemplate(param, confirmOfferRQ, bucketName))
                .flatMap(param -> getPresignedUrl(confirmOfferRQ, bucketName, param));
    }

    private Mono<RuleResponse> getTemplate(RuleResponse ruleResponse,
                                           ConfirmOfferRQ confirmOfferRQ,
                                           String bucketName) {
        String typeInsurance = confirmOfferRQ.getInsurances().get(0).getType();
        return Mono.just(ruleResponse.getData())
                .flatMap(p -> runRule(
                        ConfirmFactory.getAtributes(Constant.TYPE_INSURANCE, typeInsurance),
                        typeInsurance, Constant.RULE_FREE_INVESTMENT_VALUE))
                .flatMap(p -> {
                    String plan = p.getData().getUtilLoad().get(Constant.PLAN+""+typeInsurance).toString();
                    return runRule(
                            ConfirmFactory.getAtributes(Constant.PLAN, plan),
                            Constant.PLAN + "" + plan,
                            Constant.RULE_FREE_INVESTMENT_VALUE);
                })
                .flatMap(rul -> getdataFromCache(confirmOfferRQ, rul))
                .flatMap(p -> Mono.zip(
                        pay(ruleResponse.getData().getUtilLoad(), bucketName, p),
                        operationAnnex(ruleResponse.getData().getUtilLoad(), bucketName, p),
                        instrutionLetter(ruleResponse.getData().getUtilLoad(), bucketName, p)
                )).flatMap(p -> Mono.just(ruleResponse));
    }

    private Mono<ConfirmOfferComplete> getdataFromCache(ConfirmOfferRQ confirmOfferRQ,
                                                        RuleResponse rul) {
        ConfirmOfferComplete confirmOfferComplete = ConfirmOfferComplete.builder().build();
        String idNumber = confirmOfferRQ.getCustomer().getIdentification().getNumber();

        return Mono.just(confirmOfferRQ)
                .flatMap(ca -> customerCommercialData(idNumber, confirmOfferComplete))
                .flatMap(ca -> generalInformation(idNumber, ca))
                .flatMap(ca -> interesRate(idNumber, ca, confirmOfferRQ))
                .flatMap(ca -> Mono.just(ca.toBuilder().confirmOfferRQ(confirmOfferRQ)))
                .flatMap(ca -> Mono.just(ca.ruleResponse(rul).build()));
    }

    private Mono<ConfirmOfferComplete> interesRate(String idNumber,
                                                   ConfirmOfferComplete confirmOfferComplete,
                                                   ConfirmOfferRQ confirmOfferRQ) {
        return redisGateways
                .getData(Constant.INTERES_RATES + "" + idNumber)
                .flatMap(rates -> {
                    if (rates instanceof LoanInteresRateResponse) {
                        return Mono.just(confirmOfferComplete.toBuilder()
                                .rangeType(ConfirmFactory.getRateRange(confirmOfferRQ, (LoanInteresRateResponse) rates))
                                .build());
                    }
                    return Mono.just(confirmOfferComplete.toBuilder().build());
                });
    }

    private Mono<ConfirmOfferComplete> generalInformation(String idNumber,
                                                          ConfirmOfferComplete confirmOfferComplete) {
        return redisGateways
                .getData(Constant.BUSINESOPORTUNITI_ID + "" + idNumber)
                .flatMap(ca -> {
                    if (ca instanceof GeneralInformation) {
                        return Mono.just(confirmOfferComplete.toBuilder()
                                .generalInformation((GeneralInformation) ca).build());
                    }
                    return Mono.just(confirmOfferComplete.toBuilder().build());
                });
    }

    private Mono<ConfirmOfferComplete> customerCommercialData(String idNumber,
                                                              ConfirmOfferComplete confirmOfferComplete) {
        return redisGateways
                .getData(Constant.CUSTOMERCOMMERCIALDATA_ID + idNumber)
                .flatMap(ca -> {
                            if (ca instanceof ResponseCustomerCommercial) {
                                return Mono.just(confirmOfferComplete.toBuilder()
                                        .responseCustomerCommercial((ResponseCustomerCommercial) ca)
                                        .build());
                            }
                            return Mono.just(confirmOfferComplete.toBuilder().build());
                        }
                );
    }

    private Mono<ConfirmOfferResponse> getPresignedUrl(ConfirmOfferRQ confirmOfferRQ,
                                                       String bucketName,
                                                       RuleResponse ruleResponse) {

        List<Document> documentList = new ArrayList<>();
        Long minute = Long.parseLong(ruleResponse.getData().getUtilLoad()
                .get("").toString());

        return Mono.just(confirmOfferRQ).flatMap(p -> {

            return Mono.just(p);
        }).flatMap(p -> {

            return Mono.just(p);
        }).flatMap(p -> {

            return Mono.just(p);
        }).map(p -> ConfirmFactory.fillConfirmResponse(documentList));
    }

    private Mono<Object> pay(Map<String, Object> utilLoad,
                             String bucketName, ConfirmOfferComplete confirmOfferComplete) {

        return Mono.just(amazonS3Gateways.getObjectAsInputStream(utilLoad.get("").toString(), bucketName))
                .flatMap(p -> Mono.just(ConfirmFactory.bufferReaderToString(p)))
                .flatMap(p -> templateHtmlGateways.setDataToPay(p, utilLoad, confirmOfferComplete))
                .flatMap(p -> {
                    String namefile = Constant.PAY_LI_NATIVE_DOCUMENT + "_" +
                            confirmOfferComplete.getConfirmOfferRQ().getCustomer().getIdentification().getNumber();
                    return Mono.zip(
                            amazonS3Gateways.putObject(namefile + "." + Constant.HTML, p.getBytes(), bucketName),
                            htmlPdfGateways.htmlToPdfa1b(p)
                                    .flatMap(pd -> amazonS3Gateways
                                            .putObject(namefile + "." + Constant.PDF, pd, bucketName))
                    );
                }).flatMap(Mono::just);
    }

    private Mono<Object> operationAnnex(Map<String, Object> utilLoad, String bucketName,
                                        ConfirmOfferComplete confirmOfferComplete) {

        return Mono.just(amazonS3Gateways.getObjectAsInputStream(utilLoad.get("")
                .toString(), bucketName))
                .flatMap(p -> Mono.just(ConfirmFactory.bufferReaderToString(p)))
                .flatMap(p ->
                        templateHtmlGateways
                                .setDataOperationAnnex(p, utilLoad, confirmOfferComplete)
                )
                .flatMap(p -> {

                    String namefile = Constant.OPERATION_ANNEX_DOCUMENT + "_" + confirmOfferComplete
                            .getConfirmOfferRQ().getCustomer().getIdentification().getNumber();
                    return Mono.zip(
                            amazonS3Gateways.putObject(namefile + "." + Constant.HTML, p.getBytes(), bucketName),
                            htmlPdfGateways.htmlToPdfa1b(p)
                                    .flatMap(pd -> amazonS3Gateways
                                            .putObject(namefile + "." + Constant.PDF, pd, bucketName))
                    );
                }).flatMap(Mono::just);
    }

    private Mono<Object> instrutionLetter(Map<String, Object> utilLoad, String bucketName,
                                          ConfirmOfferComplete confirmOfferComplete) {
        return Mono.just(amazonS3Gateways.getObjectAsInputStream(utilLoad.get("")
                .toString(), bucketName))
                .flatMap(p -> Mono.just(ConfirmFactory.bufferReaderToString(p)))
                .flatMap(p -> templateHtmlGateways.setDataInstrutionLetter(p, utilLoad, confirmOfferComplete))
                .flatMap(p -> {

                    String namefile = Constant.INSTRUCTION_LETTER_DOCUMENT + "_" +
                            confirmOfferComplete.getConfirmOfferRQ().getCustomer().getIdentification().getNumber();
                    return Mono.zip(
                            amazonS3Gateways.putObject(namefile + "." + Constant.HTML, p.getBytes(), bucketName),
                            htmlPdfGateways.htmlToPdfa1b(p)
                                    .flatMap(pd -> amazonS3Gateways
                                            .putObject(namefile + "." + Constant.PDF, pd, bucketName))
                    );
                }).flatMap(Mono::just);
    }

    private Mono<RuleResponse> runRule(List<Attribute> attr, String ruleName, String productId) {
        return mapGateways.ruleValidate(RuleRequest
                .builder()
                .attributes(attr)
                .productCode(productId)
                .ruleName(ruleName).build());
    }

    private Mono<RuleResponse> runRuleAvailability() {
        RuleResponse ruleResponse = RuleResponse
                .builder()
                .data(new ResponseData(new HashMap<>(), true)).build();
        return Mono.just(ruleResponse);
    }
}
