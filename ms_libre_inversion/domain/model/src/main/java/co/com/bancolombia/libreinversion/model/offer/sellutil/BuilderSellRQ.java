package co.com.bancolombia.libreinversion.model.offer.sellutil;

import co.com.bancolombia.libreinversion.model.constants.SellConst;
import co.com.bancolombia.libreinversion.model.contactability.mail.Attached;
import co.com.bancolombia.libreinversion.model.contactability.mail.MailParameter;
import co.com.bancolombia.libreinversion.model.contactability.mail.SendEmail;
import co.com.bancolombia.libreinversion.model.contactability.mail.emailsms.BodyMailAttachedApiRQ;
import co.com.bancolombia.libreinversion.model.contactability.mail.emailsms.DataMailAttachedApiRQ;
import co.com.bancolombia.libreinversion.model.customer.CustomerFactory;
import co.com.bancolombia.libreinversion.model.docmanagement.DataReq;
import co.com.bancolombia.libreinversion.model.docmanagement.DocumentSing;
import co.com.bancolombia.libreinversion.model.docmanagement.Metadata;
import co.com.bancolombia.libreinversion.model.docmanagement.SignDocumentsReq;
import co.com.bancolombia.libreinversion.model.docmanagement.Traceability;
import co.com.bancolombia.libreinversion.model.document.InfoDocument;
import co.com.bancolombia.libreinversion.model.loansdisbursements.disbursement.response.DisbursementRS;
import co.com.bancolombia.libreinversion.model.offer.Loan;
import co.com.bancolombia.libreinversion.model.request.Destination;
import co.com.bancolombia.libreinversion.model.request.Insurances;
import co.com.bancolombia.libreinversion.model.rate.RangeType;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferComplete;
import co.com.bancolombia.libreinversion.model.request.SellOfferRQ;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn.CrearGiradorPN;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BuilderSellRQ extends SellBuildBase {

    private BuilderSellRQ() {
        super();
        throw new IllegalStateException("Utility class");
    }


    public static Optional<BodyMailAttachedApiRQ> buildBodyMailAttachedApiRQ(List<InfoDocument> docs,
                                                                             SellDto sellDto,
                                                                             ConfirmOfferComplete confirmCompl,
                                                                             DisbursementRS disbursement) {
        try {
            List<Attached> attacheds = new ArrayList<>();
            for (InfoDocument doc : docs) {
                attacheds.add(Attached.builder()
                        .attachedBase64(encodeBase64(doc.getByteArray()))
                        .attachmentName(doc.getNameFile())
                        .build());
            }
            String destination = CustomerFactory.getEmail(confirmCompl.getResponseCustomerContact(),
                    confirmCompl.getResponseRetrieveInfo());
            if (destination == null || destination.isEmpty()) {
                destination = sellDto.getSellOfferRQ().getAlternativeEmail();
            }
            BodyMailAttachedApiRQ res = BodyMailAttachedApiRQ.builder()
                    .data(DataMailAttachedApiRQ.builder()
                            .senderMail(sellConst.DOMAIN_EMAIL)
                            .subjectEmail(sellConst.SUBJECT_EMAIL)
                            .messageTemplateId(sellConst.ID_TEMPLATE_EMAIL)
                            .messageTemplateType(sellConst.MSG_TEMPLATE_EMAIL)
                            .sendEmail(arrays.asList(SendEmail.builder()
                                    .destinationEmail(destination)
                                    .parameter(buildListMailParameter(confirmCompl, sellDto.getRangeType(),
                                            disbursement, sellDto.getSellOfferRQ())).build())
                            ).attached(attacheds).build()).build();
            return Optional.of(res);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Optional.empty();
    }


    public static SignDocumentsReq buildSignDocumentsReq(InfoDocument infoDocument, CrearGiradorPN giradorPN) {
        return SignDocumentsReq.builder()
                .data(DataReq.builder()
                        .documentType(giradorPN.getDatosPersona().getIdentificacion().getTipoDocumento())
                        .documentNumber(giradorPN.getDatosPersona().getIdentificacion().getNumeroDocumento())
                        .digitalSignature(digitSignatureEnum.PASSWORD.getValue())
                        .document(DocumentSing.builder()
                                .subtypeCode("1470")
                                .fileName(infoDocument.getNameFile())
                                .file(encodeBase64(infoDocument.getByteArray()))
                                .metadata(Metadata.builder()
                                        .xTipoCliente(sellConst.DOC_MANAGE_X_TYPE_CLIENT).build())
                                .build())
                        .traceability(Traceability.builder()
                                .firmaCliente(digitSignatureEnum.PASSWORD.getValue()).build())
                        .build()).build();
    }


    public static List<MailParameter> buildListMailParameter(ConfirmOfferComplete confirmCompl, RangeType rangeType,
                                                             DisbursementRS disbursement, SellOfferRQ sellOffer) {
        final Loan loan = sellOffer.getLoans().stream().findFirst().orElse(Loan.builder().build());
        final Optional<Insurances> employment = confirmCompl.getConfirmOfferRQ().getInsurances().stream()
                .filter(insurance -> insurance.getType().equals(insuranceTypeEnum.SD.getKey())).findFirst();

        return arrays.asList(
                buidMailParameter("nombre", CustomerFactory
                        .getNames(confirmCompl.getResponseCustomerDetail())),
                buidMailParameter("cupo", confirmCompl.getConfirmOfferRQ().getOffer().getAmount()),
                buidMailParameter("cuota", ""),
                buidMailParameter("plazo", confirmCompl.getConfirmOfferRQ().getOffer().getTerm()),
                buidMailParameter("numero_credito", disbursement.getData().getLoanId()),
                buidMailParameter("periodicidad", sellConst.MONTHLY),
                buidMailParameter("dia_pago", loan.getPaymentDay().toString()),
                buidMailParameter("tasa_ea", rangeType.getEffectiveAnnualRate().toString()),
                buidMailParameter("tasa_mv", rangeType.getMonthlyRate().toString()),
                buidMailParameter("cuenta_desembolso", buildDestinationAccount(sellOffer)),
                buidMailParameter("cuenta_debito_automatico", buildAutomaticDebitAccount(loan)),
                buidMailParameter("visible", employment.isPresent() ? "" : "none")
        );
    }

    private static String buildAutomaticDebitAccount(Loan loan) {
        String idAuctDebit = "N/A";
        if (loan.getDirectDebit().getAllowDirectDebit().equals(SellConst.YES)) {
            idAuctDebit = loan.getDirectDebit().getDepositAccount().getNumber();
        }
        return idAuctDebit;
    }


    public static MailParameter buidMailParameter(String name, String value) {
        try {
            return MailParameter.builder()
                    .parameterName(name)
                    .parameterType(sellConst.TYPE_TXT)
                    .parameterValue(value).build();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private static String buildDestinationAccount(SellOfferRQ sellOffer) {
        String res = "";
        for (Loan loan : sellOffer.getLoans()) {
            for (Destination destLoan : loan.getDisbursementDestination().getDestination()) {
                if (destLoan.getBeneficiary().getIdentification().getNumber()
                        .equals(sellOffer.getCustomer().getIdentification().getNumber())) {
                    res = destLoan.getDestinationId();
                }
            }
            if (res.isEmpty()) {
                res = loan.getDisbursementDestination().getDestination().stream().findFirst().get().getDestinationId();
            }
            break;
        }
        return res;
    }
}
