package co.com.bancolombia.libreinversion.model.offer.selltestutil;

import co.com.bancolombia.libreinversion.model.account.rest.DepositAccountResponse;
import co.com.bancolombia.libreinversion.model.account.rest.Identification;
import co.com.bancolombia.libreinversion.model.beneficiary.Beneficiaries;
import co.com.bancolombia.libreinversion.model.beneficiary.Beneficiarys;
import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.constants.SellConst;
import co.com.bancolombia.libreinversion.model.contactability.mail.Attached;
import co.com.bancolombia.libreinversion.model.contactability.mail.MailParameter;
import co.com.bancolombia.libreinversion.model.contactability.mail.SendEmail;
import co.com.bancolombia.libreinversion.model.contactability.mail.emailsms.BodyMailAttachedApiRQ;
import co.com.bancolombia.libreinversion.model.contactability.mail.emailsms.BodyMailBasicApiRQ;
import co.com.bancolombia.libreinversion.model.contactability.mail.emailsms.DataMailAttachedApiRQ;
import co.com.bancolombia.libreinversion.model.contactability.mail.emailsms.DataMailBasicApiRQ;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerBasic;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerCommercial;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerCommercialData;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerContact;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerDetail;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerDetailData;
import co.com.bancolombia.libreinversion.model.docmanagement.DataReq;
import co.com.bancolombia.libreinversion.model.docmanagement.DocumentSing;
import co.com.bancolombia.libreinversion.model.docmanagement.Metadata;
import co.com.bancolombia.libreinversion.model.docmanagement.SignDocumentsReq;
import co.com.bancolombia.libreinversion.model.docmanagement.Traceability;
import co.com.bancolombia.libreinversion.model.document.InfoDocument;
import co.com.bancolombia.libreinversion.model.events.QueueMessageEmail;
import co.com.bancolombia.libreinversion.model.events.QueueMsgEmailAttached;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.Beneficiary;
import co.com.bancolombia.libreinversion.model.notification.rest.AlertIndicators;
import co.com.bancolombia.libreinversion.model.notification.rest.ResponseRetrieveInfo;
import co.com.bancolombia.libreinversion.model.notification.rest.ResponseRetrieveInfoData;
import co.com.bancolombia.libreinversion.model.offer.Offer;
import co.com.bancolombia.libreinversion.model.offer.sellutil.CacheSell;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferComplete;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferRQ;
import co.com.bancolombia.libreinversion.model.request.Destination;
import co.com.bancolombia.libreinversion.model.request.DisbursementDestination;
import co.com.bancolombia.libreinversion.model.request.Insurances;
import co.com.bancolombia.libreinversion.model.stoc.GeneralInformation;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class UtilTestSellRQ extends UtilTestBase {

    protected UtilTestSellRQ() {
        throw new IllegalStateException("Utility class");
    }

    public static ConfirmOfferComplete buildConfirmOfferComplete() {
        return ConfirmOfferComplete.builder().rangeType(buildRangeType())
                .generalInformation(GeneralInformation.builder()
                        .clientId(Integer.valueOf(ID_TEST)).build())
                .confirmOfferRQ(buildConfirmOfferRQ())
                .depositAccountResponse(DepositAccountResponse.builder().build())
                .responseCustomerBasic(ResponseCustomerBasic.builder().build())
                .responseCustomerDetail(ResponseCustomerDetail.builder()
                        .data(ResponseCustomerDetailData.builder()
                                .firstName("daniel").secondName("camilo").firstSurname("perez")
                                .build()).build())
                .responseCustomerContact(ResponseCustomerContact.builder()
                        .data(UtilTestSellRS.buildRespCustomerContData()).build())
                .responseRetrieveInfo(ResponseRetrieveInfo.builder()
                        .data(ResponseRetrieveInfoData.builder()
                                .alertIndicators(Arrays.asList(AlertIndicators.builder()
                                        .customerEmail("prueba@gmail.com")
                                        .customerMobileNumber("311835246").build())).build()).build())
                .responseCustomerCommercial(ResponseCustomerCommercial.builder()
                        .data(ResponseCustomerCommercialData.builder()
                                .fullName("nombre completo prueba").build())
                        .build())
                .build();
    }

    public static InfoDocument buildInfoDocument() {

        return InfoDocument.builder()
                .byteArray(Base64.getDecoder().decode(DocumentTestUtil.DOC))
                .author("Autor prueba")
                .creator("Creador prueba")
                .title("Titulo prueba")
                .nameFile("prueba.pdf")
                .keywords("meta data prueba").build();
    }


    public static Map<String, InfoDocument> buildFiles() {
        Map<String, InfoDocument> files = new HashMap<>();
        files.put(Constant.OPERATION_ANNEX_DOCUMENT, UtilTestSellRQ.buildInfoDocument());
        files.put(Constant.INSTRUCTION_LETTER_DOCUMENT, UtilTestSellRQ.buildInfoDocument());
        files.put(Constant.PAY_LI_NATIVE_DOCUMENT, UtilTestSellRQ.buildInfoDocument());
        files.put(Constant.WELLCOME_LETTER_DOCUMENT, UtilTestSellRQ.buildInfoDocument());
        files.put(Constant.EMPLOYMENT_INSURANCE_DOCUMENT, UtilTestSellRQ.buildInfoDocument());
        return files;
    }

    public static BodyMailAttachedApiRQ buildBodyMailAttachedApiRQ() {
        return BodyMailAttachedApiRQ.builder()
                .data(buildDataMailAttachedApiRQ()).build();
    }

    public static CacheSell buildCacheSell() {
        return CacheSell.builder()
                .isChangeOpportunities(true)
                .disbursementRS(UtilTestSellRS.buildDisbursementRS())
                .isFirmarPagare(false)
                .isExecDisbursement(false).build();

    }

    public static QueueMessageEmail buildQueueMessageEmail() {
        return QueueMessageEmail.builder()
                .messageId(MSG_ID)
                .priority(VAL_ONE_TEST.toString())
                .type(VAL_ONE_TEST.toString())
                .data(buildDataMailBasicApiRQ()).build();
    }

    public static QueueMsgEmailAttached buildQueueMsgEmailAttached() {
        return QueueMsgEmailAttached.builder()
                .messageId(MSG_ID)
                .priority(VAL_ONE_TEST.toString())
                .type(VAL_ONE_TEST.toString())
                .data(buildDataMailAttachedApiRQ()).build();
    }

    public static DataMailAttachedApiRQ buildDataMailAttachedApiRQ() {
        return DataMailAttachedApiRQ.builder()
                .senderMail(SEND_MAIL)
                .subjectEmail(REQ_LIBRE_INV)
                .messageTemplateId(MSG_ID)
                .messageTemplateType(MSG_TEMPLATE)
                .sendEmail(Arrays.asList(SendEmail.builder()
                        .destinationEmail(MAIL_TEST)
                        .parameter(Arrays.asList(MailParameter.builder()
                                .parameterName(NAME)
                                .parameterType("text")
                                .parameterValue("pruebaMAil").build())).build()))
                .attached(Arrays.asList(Attached.builder()
                        .attachmentName("archivo.pdf")
                        .attachedBase64("JVBERi0xLjcKCjEgMCBvYmogICUgZW50cnkgcG9pbnQKPDwKICAvVHlwZSAvQ2")
                        .build()))
                .build();
    }

    public static DataMailBasicApiRQ buildDataMailBasicApiRQ() {
        return DataMailBasicApiRQ.builder()
                .senderMail(SEND_MAIL)
                .subjectEmail(REQ_LIBRE_INV)
                .messageTemplateId(MSG_ID)
                .messageTemplateType(MSG_TEMPLATE)
                .sendEmail(Arrays.asList(SendEmail.builder()
                        .destinationEmail(MAIL_TEST)
                        .parameter(Arrays.asList(MailParameter.builder()
                                .parameterName(NAME)
                                .parameterType("text")
                                .parameterValue("pruebaMAil").build())).build()))
                .build();
    }

    public static SignDocumentsReq buildSignDocumentsReq() {
        return SignDocumentsReq.builder()
                .data(DataReq.builder()
                        .documentType(TYPE_DOC)
                        .documentNumber("12345698")
                        .digitalSignature("1004ttr")
                        .document(DocumentSing.builder()
                                .subtypeCode("1478")
                                .fileName("prueba.pdf")
                                .file("JVBERi0xLjcKJeLjz9MKOCAwIG9iago8PCAvVHlwZSAvUGFnZSAvUGFyZW50I")
                                .metadata(Metadata.builder()
                                        .xTipoCliente("TITULAR")
                                        .build()).build())
                        .traceability(Traceability.builder()
                                .firmaCliente("0904522").build())
                        .build())
                .build();
    }

    public static ConfirmOfferRQ buildConfirmOfferRQ() {
        return ConfirmOfferRQ.builder()
                .offer(Offer.builder()
                        .disbursementDestination(DisbursementDestination.builder()
                                .destination(getDestination()).build())
                        .paymentDay(VAL_TWO_TEST)
                        .amount("3000000")
                        .term("120").build()
                )
                .directDebit(co.com.bancolombia.libreinversion.model.request.DirectDebit.builder()
                        .allowDirectDebit(SellConst.YES)
                        .depositAccount(co.com.bancolombia.libreinversion.model.request.DepositAccount.builder()
                                .type(Constant.CUENTA_DE_AHORRO)
                                .number(NUMBER_ACCOUNT)
                                .build()).build())
                .insurances(Arrays.asList(Insurances.builder()
                        .type("SD")
                        .build())).build();
    }

    public static BodyMailBasicApiRQ buildBodyMailBasicApiRQ() {
        return BodyMailBasicApiRQ.builder().data(Arrays.asList(
                DataMailBasicApiRQ.builder()
                        .senderMail(SEND_MAIL)
                        .subjectEmail(REQ_LIBRE_INV)
                        .messageTemplateId(MSG_ID)
                        .messageTemplateType(MSG_TEMPLATE)
                        .sendEmail(Arrays.asList(SendEmail.builder()
                                .destinationEmail(MAIL_TEST)
                                .parameter(Arrays.asList(MailParameter.builder()
                                        .parameterName(NAME)
                                        .parameterType("text")
                                        .parameterValue("prueba").build())).build()))
                        .build())).build();
    }

    public static Beneficiary getAccountBeneficiary() {
        return Beneficiary.builder()
                .fullName("test")
                .identification(Identification.builder().number("2101067990").type("TIPDOC_FS001").build())
                .build();
    }

    public static List<Destination> getDestination() {

        List<Destination> destinations = new ArrayList<>();

        Destination des1 = Destination.builder()
                .amount(Double.parseDouble(AMOUNT))
                .destinationType(Constant.CUENTA_DE_AHORRO)
                .destinationId(NUMBER_ACCOUNT)
                .beneficiary(Beneficiary.builder()
                        .fullName(null)
                        .identification(Identification.builder()
                                .type(null).number(null).build()).build())
                .build();

        Destination des = Destination.builder()
                .amount(Double.parseDouble(AMOUNT))
                .destinationType(Constant.CUENTA_DE_AHORRO)
                .destinationId(NUMBER_ACCOUNT)
                .beneficiary(Beneficiary.builder()
                        .fullName("testing")
                        .identification(getIdentification()).build())
                .build();

        destinations.add(des);
        destinations.add(des1);
        return destinations;
    }

    public static Beneficiaries getBeneficiary() {

        List<Beneficiarys> beneficiarys = new ArrayList<>();
        Beneficiarys beneficiary = Beneficiarys
                .builder()
                .names("Camila")
                .surenames("Sandoval")
                .identification(getIdentification())
                .rate(new BigDecimal("100"))
                .build();
        beneficiarys.add(beneficiary);

        return Beneficiaries.builder().beneficiary(beneficiarys).build();
    }
}
