package co.com.bancolombia.libreinversion.model.request;

import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;

public class HtmlDocumentFactory {

    private static final TechLogger log = LoggerFactory.getLog(HtmlDocumentFactory.class.getName());

    private HtmlDocumentFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static String createTableHtml(ConfirmOfferComplete offer, Destination destination) {

        StringBuilder tableData = new StringBuilder();
        StringBuilder table = new StringBuilder();
        table.append(getTableHtml());

        for (Destination des : offer.getConfirmOfferRQ().getOffer()
                .getDisbursementDestination().getDestination()) {
            String newTable = table.toString();
            String dataTable = "";
            if (des.getBeneficiary().getIdentification().getNumber() != null) {
                dataTable = newTable.replace("{cuentaParaDesembolso}", des.getDestinationId())
                        .replace("{valorTotalCredito}", "$ " +
                                ConfirmFactory.formatNomberByPoint(Double.toString(des.getAmount())))
                        .replace("{nombreTitularCuenta}", des.getBeneficiary().getFullName())
                        .replace("{numeroIdentificacion}",
                                ConfirmFactory.formatNomberByPoint(des.getBeneficiary()
                                        .getIdentification().getNumber()))
                        .replace("{tipoCuentaDesembolso}", ConfirmFactory.getAccountType(des.getDestinationType(),
                                offer.getRuleResponse().getData()));
            } else {
                dataTable = newTable.replace("{cuentaParaDesembolso}", destination.getDestinationId())
                        .replace("{valorTotalCredito}",  "$ " +
                                ConfirmFactory.formatNomberByPoint(Double.toString(des.getAmount())))
                        .replace("{nombreTitularCuenta}",
                                offer.getResponseCustomerCommercial().getData().getFullName())
                        .replace("{numeroIdentificacion}",
                                ConfirmFactory.formatNomberByPoint(
                                        offer.getConfirmOfferRQ().getCustomer().getIdentification().getNumber()))
                        .replace("{tipoCuentaDesembolso}", ConfirmFactory.getAccountType(des.getDestinationType(),
                                offer.getRuleResponse().getData()));
            }
            tableData.append(dataTable);
        }
        return tableData.toString();
    }

    private static String getTableHtml() {
        InputStream inputStream = null;
        try {
            inputStream = new ClassPathResource("tabla-terceros.txt").getInputStream();
        } catch (IOException e) {
        }
        return ConfirmFactory.bufferReaderToString(inputStream);
    }

    public static Mono<ConfirmOfferRQ> validateAmmount(ConfirmOfferRQ confirmOfferRQ, String messageId) {

        long sumAmmount = confirmOfferRQ.getOffer().getDisbursementDestination().getDestination()
                .stream().mapToLong(amo -> (long) amo.getAmount()).sum();
        if (sumAmmount > Double.parseDouble(confirmOfferRQ.getOffer().getAmount())
                || sumAmmount < Double.parseDouble(confirmOfferRQ.getOffer().getAmount())){
            return Mono.error(new LibreInversionException(ErrorEnum.MSG_LI023.getName(),
                    ErrorEnum.MSG_LI023.getName(), ErrorEnum.MSG_LI023.getMessage(), "", "", messageId));
        }

        return Mono.just(confirmOfferRQ);
    }
}
