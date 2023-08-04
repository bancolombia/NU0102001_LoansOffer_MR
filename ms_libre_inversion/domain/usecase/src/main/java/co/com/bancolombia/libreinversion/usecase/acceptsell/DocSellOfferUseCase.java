package co.com.bancolombia.libreinversion.usecase.acceptsell;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.DocumentUtil;
import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.constants.SellConst;
import co.com.bancolombia.libreinversion.model.docmanagement.SignDocumentsReq;
import co.com.bancolombia.libreinversion.model.document.InfoDocument;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.response.DisbursementRS;
import co.com.bancolombia.libreinversion.model.offer.sellutil.BuilderSellRQ;
import co.com.bancolombia.libreinversion.model.offer.sellutil.UtilSell;
import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferComplete;
import co.com.bancolombia.libreinversion.model.request.SellOfferRQ;
import co.com.bancolombia.libreinversion.model.request.gateways.AmazonS3Gateways;
import co.com.bancolombia.libreinversion.model.request.gateways.DocManagementGateway;
import co.com.bancolombia.libreinversion.model.request.gateways.HtmlPdfGateways;
import co.com.bancolombia.libreinversion.model.request.gateways.TemplateHtmlGateways;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn.CrearGiradorPN;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class DocSellOfferUseCase {

    private final TemplateHtmlGateways templateHtmlGateways;
    private final HtmlPdfGateways htmlPdfGateways;
    private final DocManagementGateway docContributionGateway;
    private final AmazonS3Gateways amazonS3Gateways;
    private static TechLogger log = LoggerFactory.getLog(DocSellOfferUseCase.class.getName());

    public Mono<Boolean> loadDocumentsLiNativo(String bucketName, Map<String, InfoDocument> docs) {
        InfoDocument operation = docs.get(Constant.OPERATION_ANNEX_DOCUMENT);
        InfoDocument instruction = docs.get(Constant.INSTRUCTION_LETTER_DOCUMENT);
        InfoDocument pay = docs.get(Constant.PAY_LI_NATIVE_DOCUMENT);
        return Mono.zip(
                awsS3PutObject(operation, bucketName),
                awsS3PutObject(instruction, bucketName),
                awsS3PutObject(pay, bucketName)
        ).flatMap(zip -> Mono.just(true));
    }

    public Mono<Map<String, InfoDocument>> getDocumentsLiNativo(String bucketName, SellOfferRQ sellOfferRQ) {

        return Mono.zip(
                getDocumentsS3(bucketName, "nameFileOpe"),
                getDocumentsS3(bucketName, "nameFileOpe"),
                getDocumentsS3(bucketName, "nameFileOpe")
        ).flatMap(bytes -> {
            Map<String, InfoDocument> documens = new HashMap<>();

            return Mono.just(documens);
        }).onErrorResume(e -> Mono.error(UtilSell.buildErr(e.getMessage(), "")));
    }

    public Mono<InfoDocument> getWellcomeTemplateEmail(String bucketName, SellOfferRQ sellOfferRQ,
                                                       ConfirmOfferComplete confirmOfferComplete) {
        String nameFileTemplateWell = Constant.TEMPLATE_WELLCOME_DOCUMENT + ".html";
        return getDocumentsS3(bucketName, nameFileTemplateWell)
                .flatMap(templ -> {
                    String htmlTemplate = new String(templ, StandardCharsets.UTF_8);
                    return templateHtmlGateways.setDataTemplateWeelcome(htmlTemplate, confirmOfferComplete)
                            .flatMap(doc -> {
                                InfoDocument docInf = buildInfoDoc(sellOfferRQ, nameFileTemplateWell,
                                        doc.getBytes(StandardCharsets.UTF_8));
                                return Mono.just(docInf);
                            });
                });
    }

    private InfoDocument getDocAttacment(byte[] signDocumentsResp, InfoDocument infoDocument) {
        infoDocument.setByteArray(signDocumentsResp);
        return infoDocument;
    }

    public Mono<InfoDocument> mergeDocLiNativo(Map<String, InfoDocument> files, String nameDocument) {
        return Mono.just(files).flatMap(ltsFile -> {
            InfoDocument operation = files.get(Constant.OPERATION_ANNEX_DOCUMENT);
            InfoDocument letter = files.get(Constant.INSTRUCTION_LETTER_DOCUMENT);
            InfoDocument pay = files.get(Constant.PAY_LI_NATIVE_DOCUMENT);
            List<InfoDocument> docs = Arrays.asList(operation, letter, pay);
            InfoDocument res = DocumentUtil.mergePdfFiles(docs, nameDocument + ".pdf");

            return res != null ? Mono.just(res) : Mono.error(UtilSell.buildMsgErrProcessDco(""));
        });
    }

    public Mono<InfoDocument> mergeDocEmployment(Map<String, InfoDocument> docEmployment) {
        return Mono.just(docEmployment).flatMap(infoDocumentMap -> {
            LocalDateTime date = LocalDateTime.now();
            String dateStr = date.getDayOfMonth() + "-" + date.getDayOfMonth() + "-" + date.getYear();
            InfoDocument letterEmp = docEmployment.get(Constant.WELLCOME_LETTER_DOCUMENT);
            InfoDocument insurance = docEmployment.get(Constant.EMPLOYMENT_INSURANCE_DOCUMENT);
            List<InfoDocument> docs = Arrays.asList(letterEmp, insurance);
            InfoDocument res = DocumentUtil
                    .mergePdfFiles(docs, Constant.NAME_MERGE_DOC_EMPLOYMENT + "_" + dateStr + ".pdf");

            return res != null ? Mono.just(res) : Mono.error(UtilSell.buildMsgErrProcessDco(""));
        });
    }

    public Mono<Map<String, InfoDocument>> getDocumentsEmployment(String bucketName, SellOfferRQ sellOfferPgc,
                                                                  ConfirmOfferComplete confirmOfferComplete,
                                                                  RuleResponse paramSellOffer,
                                                                  DisbursementRS disbursementRS) {
        String nameS3FileWell = Constant.WELLCOME_LETTER_DOCUMENT + ".html";
        String nameS3FileEmplInsu = Constant.EMPLOYMENT_INSURANCE_DOCUMENT + ".pdf";
        String codCardif = paramSellOffer.getData().getUtilLoad().get(SellConst.MAP_CARDIF_COD_EMPLOYMENT).toString();
        return Mono.zip(
                getDocumentsS3(bucketName, nameS3FileWell),
                getDocumentsS3(bucketName, nameS3FileEmplInsu)
        ).flatMap(bytes -> {
            String htmlLetter = new String(bytes.getT1(), StandardCharsets.UTF_8);
            return templateHtmlGateways.setDataEmploymentInsuraceLetter(htmlLetter,
                            confirmOfferComplete, codCardif, disbursementRS)
                    .flatMap(fileStrData -> {
                        LocalDateTime date = LocalDateTime.now();
                        String dateStr = date.getDayOfMonth() + "-" + date.getDayOfMonth() + "-" + date.getYear();
                        String nameFileWellPdf = Constant.WELLCOME_LETTER_DOCUMENT + "_" + dateStr + ".pdf";
                        String nameFileEmplInsu = Constant.EMPLOYMENT_INSURANCE_DOCUMENT + "_" + dateStr + ".pdf";
                        byte[] lettterPdf = htmlPdfGateways.basicHtmlToPdf(fileStrData, nameFileWellPdf);
                        Map<String, InfoDocument> documens = new HashMap<>();
                        documens.put(Constant.WELLCOME_LETTER_DOCUMENT,
                                buildInfoDoc(sellOfferPgc, nameFileWellPdf, lettterPdf));
                        documens.put(Constant.EMPLOYMENT_INSURANCE_DOCUMENT,
                                buildInfoDoc(sellOfferPgc, nameFileEmplInsu, bytes.getT2()));

                        return Mono.just(documens);
                    });
        });
    }

    public Mono<Map<String, InfoDocument>> addMetaDataPropDocument(Map<String, InfoDocument> docLiNativo) {
        try {
            return Mono.just(docLiNativo).flatMap(files -> {
                InfoDocument operation = addMetaData(files.get(Constant.OPERATION_ANNEX_DOCUMENT));
                InfoDocument instruction = addMetaData(files.get(Constant.INSTRUCTION_LETTER_DOCUMENT));
                InfoDocument pay = addMetaData(files.get(Constant.PAY_LI_NATIVE_DOCUMENT));
                files.put(Constant.OPERATION_ANNEX_DOCUMENT, operation);
                files.put(Constant.INSTRUCTION_LETTER_DOCUMENT, instruction);
                files.put(Constant.PAY_LI_NATIVE_DOCUMENT, pay);
                return Mono.just(files);
            });
        } catch (IndexOutOfBoundsException e) {
            return Mono.error(new LibreInversionException(ErrorEnum.MSG_GP001.getName(),
                    ErrorEnum.MSG_GP001.getName(), e.getMessage(), "", "", ""));
        }
    }

    public InfoDocument buildInfoDoc(SellOfferRQ sellOfferRQ, String nameFile, byte[] array) {
        return InfoDocument.builder()
                .nameFile(nameFile)
                .keywords(sellOfferRQ.getLegalTrace())
                .byteArray(array).build();
    }

    public InfoDocument addPassDocument(InfoDocument doc, SellOfferRQ sellOfferRQ) {
        try {
            String idNumber = sellOfferRQ.getCustomer().getIdentification().getNumber();
            byte[] docWithPass = DocumentUtil.addPasswordPdf(idNumber, idNumber, doc.getByteArray());
            doc.setByteArray(docWithPass);
            return doc;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public Mono<Boolean> awsS3PutObject(InfoDocument infoDocument, String bucketName) {
        LibreInversionException err = new LibreInversionException(Constant.AWS_S3_CLIENT_ERROR,
                ErrorEnum.MSG_LI017.getMessage(),
                "Archivo no cargado : " + infoDocument.getNameFile(), SellConst.SELL_OFFER, SellConst.SELL_OFFER, "");
        try {
            return amazonS3Gateways.putObject(infoDocument.getNameFile(), infoDocument.getByteArray(), bucketName)
                    .flatMap(isLoad -> {
                        if (!isLoad.booleanValue()) {
                            return Mono.error(() -> err);
                        }
                        return Mono.just(isLoad);
                    }).onErrorResume(e -> {
                        log.error(e.getMessage());
                        return Mono.error(err);
                    });
        } catch (Exception e) {
            log.error(e);
            return Mono.error(UtilSell.buildErr(e.getMessage(), ""));
        }
    }

    public String generatePresignedURLDocsLi(String bucketName, String nameFile, Long minutes) {
        return amazonS3Gateways.generatePresignedURL(bucketName, nameFile, minutes);
    }


    public Mono<byte[]> getDocumentsS3(String bucketName, String nameFile) {
        try {
            return amazonS3Gateways.getObjectAsByteArray(nameFile.trim(), bucketName)
                    .onErrorResume(e -> {
                        log.error(e.getMessage());
                        return Mono.error(UtilSell.buildErr(UtilSell.buildMsgErrS3DocNotFound(e, nameFile), ""));
                    });
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
            return Mono.error(UtilSell.buildErr(UtilSell.buildMsgErrS3DocNotFound(e, nameFile), ""));
        }
    }

    public InfoDocument addMetaData(InfoDocument doc) {
        doc.setAuthor(Constant.AUTOR_DOC);
        doc.setCreator(Constant.AUTOR_DOC);
        doc = DocumentUtil.addMetaDataFromInfoDocument(doc);
        return doc;
    }

}
