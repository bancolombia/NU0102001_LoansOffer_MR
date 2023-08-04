package co.com.bancolombia.libreinversion.api.handler;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.constants.SellConst;
import co.com.bancolombia.libreinversion.model.contactability.mail.emailsms.BodyMailAttachedApiRQ;
import co.com.bancolombia.libreinversion.model.document.Document;
import co.com.bancolombia.libreinversion.model.document.InfoDocument;
import co.com.bancolombia.libreinversion.model.enums.FuncPagareEnum;
import co.com.bancolombia.libreinversion.model.enums.InsuranceTypeEnum;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.DisbursementRQ;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.response.DisbursementRS;
import co.com.bancolombia.libreinversion.model.offer.LoanSell;
import co.com.bancolombia.libreinversion.model.offer.SellOfferData;
import co.com.bancolombia.libreinversion.model.offer.selltestutil.UtilTestSellRS;
import co.com.bancolombia.libreinversion.model.offer.sellutil.BuildDeceval;
import co.com.bancolombia.libreinversion.model.offer.sellutil.BuildDisbursement;
import co.com.bancolombia.libreinversion.model.offer.sellutil.BuilderSellRQ;
import co.com.bancolombia.libreinversion.model.offer.sellutil.SellDto;
import co.com.bancolombia.libreinversion.model.offer.sellutil.UtilSell;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferComplete;
import co.com.bancolombia.libreinversion.model.request.SellOfferRQ;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.header.RequestHeader;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn.CrearGiradorPN;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.createpay.CreatePay;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.signaturepay.FirmarPagare;
import co.com.bancolombia.libreinversion.model.stoc.GeneralInformation;
import co.com.bancolombia.libreinversion.usecase.acceptsell.AcceptSellUseCase;
import co.com.bancolombia.libreinversion.usecase.acceptsell.DocSellOfferUseCase;
import co.com.bancolombia.libreinversion.usecase.contactability.ContactabilityUseCase;
import co.com.bancolombia.libreinversion.usecase.disbursements.DisbursementsUseCase;
import co.com.bancolombia.libreinversion.usecase.soap.GestionarPagareElectUseCase;
import co.com.bancolombia.libreinversion.usecase.stoc.OpportunitiesUseCase;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class SellHandler {

    private final AcceptSellUseCase acceptSellUseCase;
    private final GestionarPagareElectUseCase gestPagareElectUseCase;
    private final ContactabilityUseCase contactabilityUseCase;
    private final DocSellOfferUseCase docSellOUseCase;
    private final DisbursementsUseCase disbursementsUseCase;
    private static final TechLogger log = LoggerFactory.getLog(SellHandler.class.getName());
    private String bucketName;
    private static final String OPERATION = "";


    @NonNull
    public Mono<ServerResponse> listenSell(ServerRequest serverRequest) {
        final String idTest = "";
        return serverRequest.bodyToMono(SellOfferRQ.class).flatMap(sellOfferRQ -> {
            LibreInversionException err = new LibreInversionException(Constant.MDM_CLIENT_REQ_NOT_FOUND, "001",
                    Constant.MDM_CLIENT_REQ_NOT_FOUND, Constant.MDM_CLIENT_REQ_NOT_FOUND, "sell", "");

            return sellOfferRQ.getCustomer() == null ? Mono.error(err) : Mono.just(sellOfferRQ);
        }).flatMap(sellOfferRQ -> getHeadReq(serverRequest, Constant.MESSAGE_ID)
                .flatMap(msgId -> getMapRules(msgId).flatMap(sell -> {
                    sell.setMessageId(msgId);
                    sell.setSellOfferRQ(sellOfferRQ);

                    if (sellOfferRQ.getCustomer().getIdentification().getNumber().equals(idTest)) {
                        Mono<SellOfferData> data = Mono.just(UtilTestSellRS.buildSellOfferData());
                        return acceptSellUseCase.setCaheQAndOperation(sellOfferRQ, 1000L)
                                .flatMap(sellRQ -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                                        .body(data, SellOfferData.class));
                    } else {
                        return getResponseBodySellOffer(sell)
                                .flatMap(sellDto -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                                        .body(responseSellOffer(sellDto), SellOfferData.class));
                    }
                })));
    }

    private Mono<SellDto> getResponseBodySellOffer(SellDto sell) {
        return Mono.just(sell.getSellOfferRQ())
                .flatMap(sellOfferRQ -> acceptSellUseCase.setCaheQAndOperation(sellOfferRQ, sell.getTimeCache()))
                .flatMap(this::validAvailabilityApp)
                .flatMap(acceptSellUseCase::queryRuleParamPgc)
                .flatMap(sellOfferPgc -> gestPagareElectUseCase
                        .getCompleteDataCache(sellOfferPgc.getCustomer().getIdentification().getNumber())
                        .flatMap(confirmComplete -> Mono.zip(
                                docSellOUseCase.getDocumentsLiNativo(bucketName, sellOfferPgc),
                                acceptSellUseCase.loadCacheSell(sellOfferPgc)
                        ).flatMap(zip -> {
                            sell.setSellOfferRQ(sellOfferPgc);
                            sell.setDocLiNativo(zip.getT1());
                            sell.setCacheSell(zip.getT2());
                            log.error(sell);
                            return acceptSellUseCase.getInteresRateCache(confirmComplete, sellOfferPgc)
                                    .flatMap(rangeType -> acceptSellUseCase.getloanType(confirmComplete, sellOfferPgc)
                                            .flatMap(typeLoan -> {
                                                sell.setRangeType(rangeType);
                                                sell.setCreditPlan(typeLoan);
                                                return executeSellOffer(confirmComplete, sell);
                                            }));
                        })));
    }

    private Mono<SellDto> getMapRules(String msgId) {
        final SellDto sell = SellDto.builder().build();
        return Mono.zip(
                acceptSellUseCase.getTimeUrlPreSigned(msgId),
                acceptSellUseCase.getTimeUrlPreSigned(msgId)
        ).flatMap(zip -> {
            return Mono.just(sell);
        });
    }

    private Mono<SellDto> executeSellOffer(ConfirmOfferComplete confirmComplete, SellDto sellDto) {

        Optional<CrearGiradorPN> girador = BuildDeceval
                .buildCrearGiradorPN(confirmComplete, sellDto.getSellOfferRQ(), sellDto.getRuleDeceval());

        return girador.map(crearGiradorPN -> executeDocManage(crearGiradorPN, sellDto).flatMap(mapDocSingDoc -> {
            sellDto.setDocLiNativo(mapDocSingDoc);
            return executeDeceval(crearGiradorPN, sellDto).flatMap(isDeceval -> {
                if (!isDeceval.booleanValue()) {
                    return Mono.error(UtilSell
                            .errorProcess(OPERATION, sellDto.getMessageId(), null));
                }

                return executeDisbursement(sellDto, confirmComplete)
                        .flatMap(disbursementRS -> executeEmploymentProcess(sellDto, confirmComplete, disbursementRS)
                                .flatMap(sellDtoEmployment -> sendNotification(
                                        disbursementRS,
                                        sellDtoEmployment,
                                        confirmComplete
                                ).flatMap(sellDtoMergeDocLi -> executeStoc(
                                        confirmComplete.getGeneralInformation(), sellDtoMergeDocLi
                                ).flatMap(isStocChange -> Mono.just(sellDtoMergeDocLi)))));
            });
        })).orElseGet(() -> Mono.error(SellConst.dataClientErrorProcess(OPERATION, sellDto.getMessageId())));
    }

    private Mono<SellOfferRQ> validAvailabilityApp(SellOfferRQ req) {
        LibreInversionException err = UtilSell.errorProcess(OPERATION, "", null);

        return acceptSellUseCase.validAvailabilityApp(req)
                .flatMap(rule -> rule.getData().isValid() ? Mono.just(req) : Mono.error(err));
    }

    private Mono<Map<String, InfoDocument>> executeDocManage(CrearGiradorPN girador, SellDto sellDto) {
        return docSellOUseCase.addMetaDataPropDocument(sellDto.getDocLiNativo())
                .flatMap(filesMetaData -> docSellOUseCase.loadFileManageDocSign(filesMetaData, girador,
                                sellDto.getParamDocumentManage(), sellDto.getMessageId())
                        .flatMap(docManageDocSignMap -> docSellOUseCase
                                .loadDocumentsLiNativo(bucketName, docManageDocSignMap)
                                .flatMap(isLodad -> Mono.just(docManageDocSignMap)))
                );
    }

    private Mono<Boolean> executeDeceval(CrearGiradorPN girador, SellDto sellDto) {
        final String method = "executeDeceval";
        Optional<RequestHeader> headerCrear = Optional.ofNullable(RequestHeader.builder().build());
        Optional<RequestHeader> headerPay = Optional.ofNullable(RequestHeader.builder().build());
        Optional<RequestHeader> headerFirma = Optional.ofNullable(RequestHeader.builder().build());

        if (!headerCrear.isPresent() || !headerPay.isPresent() || !headerFirma.isPresent()) {
            return Mono.error(SellConst.dataClientErrorProcess(method, sellDto.getMessageId()));
        }
        return executeCrearGirador(girador, sellDto, headerCrear.get())
                .flatMap(pagare -> executeCrearPagare(pagare, girador, sellDto, headerPay.get())
                        .flatMap(firma -> executeFirmarPagare(firma, headerFirma.get(), sellDto)));
    }

    private Mono<CreatePay> executeCrearGirador(CrearGiradorPN girador, SellDto sellDto,
                                                RequestHeader headerCrear) {
        return gestPagareElectUseCase.crearGiradorPN(girador, headerCrear)
                .flatMap(pnResp -> {
                    InfoDocument payDoc = sellDto.getDocLiNativo().get(Constant.PAY_LI_NATIVE_DOCUMENT);
                    Optional<CreatePay> pagare = BuildDeceval.buildCreatePay(girador, payDoc);

                    return Mono.just(pagare.get());
                });
    }

    private Mono<FirmarPagare> executeCrearPagare(CreatePay pagare, CrearGiradorPN girador,
                                                  SellDto sellDto, RequestHeader headerPay) {
        return gestPagareElectUseCase.crearPagare(pagare, headerPay)
                .flatMap(pagResp -> {
                    if (pagResp.getDatosPagareRespuesta().getIdPagareDeceval() == null) {
                        return Mono.error(new LibreInversionException(Constant.DECEVAL_CLIENT_ERROR, "001",
                                Constant.DECEVAL_CLIENT_ERROR, pagResp
                                .getDatosPagareRespuesta().getMensajeRespuesta(), "sell", ""));
                    }
                    Optional<FirmarPagare> firma = BuildDeceval
                            .bulidFirmarPagare(pagResp, girador, pagare, sellDto.getSellOfferRQ());

                    if (!firma.isPresent()) {
                        return Mono.error(SellConst.dataClientErrorProcess(
                                "executeCrearGirador", sellDto.getMessageId()));
                    }
                    return Mono.just(firma.get());
                });
    }

    private Mono<Boolean> executeFirmarPagare(FirmarPagare firma, RequestHeader headerFirma, SellDto sellDto) {
        //prueba temporal
        sellDto.getCacheSell().setFirmarPagare(true);
        //---
        if (!sellDto.getCacheSell().isFirmarPagare()) {
            return gestPagareElectUseCase.firmaPagare(firma, headerFirma)
                    .flatMap(s -> {
                        log.info("firmarPagare: " + s);
                        sellDto.getCacheSell().setFirmarPagare(true);
                        sellDto.getCacheSell().setCreatrePagare(true);

                        return acceptSellUseCase
                                .updateCacheSell(
                                        sellDto.getCacheSell(),
                                        sellDto.getSellOfferRQ(),
                                        sellDto.getTimeCache()
                                );
                    });
        } else {
            return Mono.just(true);
        }
    }

    private Mono<Boolean> executeStoc(GeneralInformation generalInformation, SellDto sellDto) {
        //prueba temporal
        sellDto.getCacheSell().setChangeOpportunities(true);
        //*********
        if (!sellDto.getCacheSell().isChangeOpportunities()) {
            return Mono.just(true);
        } else {
            return Mono.just(true);
        }
    }

    private Mono<SellDto> executeEmploymentProcess(SellDto sellDto, ConfirmOfferComplete confirmOfferComplete,
                                                   DisbursementRS disbursementRS) {
        sellDto.setEmployment(confirmOfferComplete.getConfirmOfferRQ().getInsurances()
                .stream().filter(insurance -> insurance.getType()
                        .equals(InsuranceTypeEnum.SD.getKey())).findFirst());

        return Mono.just(sellDto).flatMap(sell -> {
            if (sell.getEmployment().isPresent()) {
                return docSellOUseCase
                        .getDocumentsEmployment(bucketName, sellDto.getSellOfferRQ(), confirmOfferComplete,
                                sellDto.getParamSellOffer(), disbursementRS)
                        .flatMap(employmentDocMap -> docSellOUseCase.mergeDocEmployment(employmentDocMap)
                                .flatMap(mergeDocument -> {
                                    sell.setEmploymentDocMerge(mergeDocument);
                                    return Mono.just(sell);
                                }));
            }
            return Mono.just(sell);
        });
    }

    private Mono<SellDto> sendNotification(DisbursementRS disbursement, SellDto sellDto,
                                           ConfirmOfferComplete confirmComplete) {
        String nameDocument = UtilSell.validObjectRuleNull(sellDto.getParamSellOffer()
                .getData().getUtilLoad().get(SellConst.NAME_DOCUMENT_SELL).toString());
        return Mono.just(sellDto).flatMap(sellOfferRQ -> docSellOUseCase
                .mergeDocLiNativo(sellDto.getDocLiNativo(), nameDocument).flatMap(mergeLiNat -> {
                    List<InfoDocument> sendDocs = new ArrayList<>();
                    InfoDocument liNativoPass = docSellOUseCase.addPassDocument(mergeLiNat, sellDto.getSellOfferRQ());

                    if (liNativoPass.getByteArray() == null) {
                        return Mono.error(UtilSell.buildMsgErrProcessDco(sellDto.getMessageId()));
                    }
                    return docSellOUseCase.awsS3PutObject(liNativoPass, bucketName).flatMap(isload -> {
                        sellDto.setMergeDocLiNativo(liNativoPass);
                        sendDocs.add(liNativoPass);
                        if (sellDto.getEmployment().isPresent()) {
                            InfoDocument employmentPass = docSellOUseCase
                                    .addPassDocument(sellDto.getEmploymentDocMerge(), sellDto.getSellOfferRQ());
                            sendDocs.add(employmentPass);
                        }

                        Optional<BodyMailAttachedApiRQ> dataEmail = BuilderSellRQ.buildBodyMailAttachedApiRQ(
                                sendDocs, sellDto, confirmComplete, disbursement
                        );

                        return contactabilityUseCase
                                .sendAttachmentMail(sellDto.getMessageId(), SellConst.PRIORITY_TWO, dataEmail.get())
                                .flatMap(s -> Mono.just(sellDto));
                    });
                }));
    }

    private Mono<DisbursementRS> executeDisbursement(SellDto sellDto, ConfirmOfferComplete confirmComplete) {
        return Mono.just(sellDto).flatMap(sell -> {
            Optional<DisbursementRQ> disbursement = BuildDisbursement
                    .buildDisbursementRQ(confirmComplete, sellDto.getSellOfferRQ(), sellDto.getRangeType(),
                            sellDto.getCreditPlan());

            if (!disbursement.isPresent()) {
                return Mono.error(SellConst
                        .dataClientErrorProcess("executeDisbursement", sellDto.getMessageId()));
            }
            return acceptSellUseCase.validAmmounts(confirmComplete, sellDto.getSellOfferRQ(), sellDto.getMessageId())
                    .flatMap(cumple -> {
                        if (!sell.getCacheSell().isExecDisbursement()) {
                            return disbursementsUseCase.execcuteDisbursement(disbursement.get(), sellDto.getMessageId())
                                    .flatMap(disbursementRS -> {
                                        sellDto.getCacheSell().setExecDisbursement(true);
                                        sellDto.getCacheSell().setDisbursementRS(disbursementRS);
                                        return acceptSellUseCase.updateCacheSell(
                                                sellDto.getCacheSell(), sellDto.getSellOfferRQ(), sellDto.getTimeCache()
                                        ).flatMap(aBoolean -> Mono.just(disbursementRS));
                                    });
                        } else {
                            return Mono.just(sell.getCacheSell().getDisbursementRS());
                        }
                    });
        });
    }

    private Mono<SellOfferData> responseSellOffer(SellDto sell) {
        String urlDocs = docSellOUseCase.generatePresignedURLDocsLi(
                bucketName, sell.getMergeDocLiNativo().getNameFile(), sell.getTimeUrlPreSigned()
        );
        SellOfferData sellOfferData = SellOfferData.builder()
                .loans(LoanSell.builder()
                        .loanId(sell.getCacheSell().getDisbursementRS().getData().getLoanId())
                        .receipt("12345678901234567890").build())
                .documents(Arrays.asList(Document.builder()
                        .format(Constant.PDF)
                        .name(sell.getMergeDocLiNativo().getNameFile())
                        .url(urlDocs).build()))
                .build();

        return Mono.just(sell.getSellOfferRQ())
                .flatMap(sellOfferRQ -> acceptSellUseCase
                        .setCaheQAndOperation(sell.getSellOfferRQ(), sell.getTimeCache()))
                .thenReturn(sellOfferData);
    }


    private Mono<String> getHeadReq(ServerRequest serverRequest, String header) {
        List<String> attr = serverRequest.headers().header(header);
        String notFount = " not found ";
        return Mono.just(notFount);
    }
}
