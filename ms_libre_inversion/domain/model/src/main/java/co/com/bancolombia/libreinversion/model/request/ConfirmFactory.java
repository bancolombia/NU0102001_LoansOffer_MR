package co.com.bancolombia.libreinversion.model.request;

import co.com.bancolombia.libreinversion.model.commons.AccountType;
import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.document.Document;
import co.com.bancolombia.libreinversion.model.product.Amount;
import co.com.bancolombia.libreinversion.model.product.Attribute;
import co.com.bancolombia.libreinversion.model.product.ResponseData;
import co.com.bancolombia.libreinversion.model.rate.LoanInteresRateResponse;
import co.com.bancolombia.libreinversion.model.rate.RangeType;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ConfirmFactory {

    private static final TechLogger log = LoggerFactory.getLog(ConfirmFactory.class.getName());

    private ConfirmFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static Mono<Boolean> validateBeneficiary() {
        return Mono.just(true);
    }

    public static ConfirmOfferResponse fillConfirmResponse(List<Document> documentList) {

        DocumentResponse documentResponse = DocumentResponse.builder()
                .document(documentList)
                .build();

        ConfirmResponseData confirmResponseData = ConfirmResponseData.builder()
                .documents(documentResponse)
                .build();

        return ConfirmOfferResponse.builder()
                .data(confirmResponseData)
                .build();
    }

    public static String bufferReaderToString(InputStream inputStream) {
        String html = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            html = br.lines().parallel().collect(Collectors.joining("\n"));
        } catch (RuntimeException e) {
        }
        return html;
    }

    public static List<Attribute> getAtributes(String name, String value) {
        return Arrays.asList(Attribute.builder().name(name)
                .value(value).build());
    }

    public static RangeType getRateRange(ConfirmOfferRQ confirmOfferRQ, LoanInteresRateResponse rates) {
        return rates.getData().getRateRange().get(0)
                .getRangeType()
                .stream().filter(rate ->
                        (rate.getMinimumAmount().intValue()
                                <= new BigDecimal(confirmOfferRQ.getOffer().getAmount()).intValue()
                                &&
                                rate.getMaximumAmount().intValue()
                                        >= new BigDecimal(confirmOfferRQ.getOffer().getAmount()).intValue())
                ).collect(Collectors.toList()).get(0);
    }

    public static String getDateDiferentInDay(String startDate) {

        Date d1 = null;
        Date d2 = null;
        long differenceInDays = 0;
        SimpleDateFormat sdf = new SimpleDateFormat(Constant.DATE_FORMAT);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(Constant.DATE_FORMAT);
        LocalDateTime now = LocalDateTime.now();

        try {
            d1 = sdf.parse(startDate);
            d2 = sdf.parse(dtf.format(now));
            long differenceInTime = d2.getTime() - d1.getTime();
            differenceInDays = (differenceInTime /
                    (Constant.TIME_SECOND * Constant.TIME_MINUTE *
                            Constant.TIME_MINUTE * Constant.TIME_HOUR));
        } catch (ParseException e) {
        }

        return String.valueOf(differenceInDays);
    }

    public static String convertDate(String date) {
        return date.replace("-", "/");
    }

    public static String addPercentSimbol(String number) {
        return convertToPercent(number) + " %";
    }

    public static String convertToPercent(String number) {
        double percent = (Double.parseDouble(number) * Constant.MAX_PERCENT);
        return Double.toString(percent);
    }

    public static String formatNomberByPoint(String number) {
        DecimalFormat df = new DecimalFormat("###,###.###");
        String numberFormat = df.format(Double.parseDouble(number.replace(",", "").replace(".", "")));
        return numberFormat.replace(",", ".");
    }

    public static String stringFormatNomberByPoint(String number) {
        DecimalFormat df = new DecimalFormat("###,###,###");
        String numberFormat = df.format(Integer.parseInt(number.replace(",", "").replace(".", "")));
        return numberFormat.replace(",", ".");
    }

    public static String increaseDate(String months) {
        return LocalDate.now().plusMonths(Long.parseLong(months)).toString();
    }

    public static Document setLinkDocument(String url, String nameDocument, String format) {
        return Document.builder()
                .url(url)
                .format(format)
                .name(nameDocument)
                .build();
    }

    public static String[] getAutomaticDebitAccount(ConfirmOfferComplete confirm) {
        if (confirm.getConfirmOfferRQ().getDirectDebit().getDepositAccount().getNumber() == null) {
            Destination destination = confirm.getConfirmOfferRQ().getOffer().getDisbursementDestination()
                    .getDestination().stream().filter(p -> p.getBeneficiary().getIdentification().getNumber() == (null))
                    .collect(Collectors.toList()).get(0);
            return new String[]{destination.getDestinationId(), destination.getDestinationType()};
        } else {
            return new String[]{
                    confirm.getConfirmOfferRQ().getDirectDebit().getDepositAccount().getNumber(),
                    confirm.getConfirmOfferRQ().getDirectDebit().getDepositAccount().getType()
            };
        }
    }

    public static Destination getAccountOwner(ConfirmOfferComplete confirm) {
        return confirm.getConfirmOfferRQ().getOffer().getDisbursementDestination()
                .getDestination().stream().filter(p -> p.getBeneficiary().getIdentification().getNumber() == null)
                .collect(Collectors.toList()).get(0);
    }

    public static String replaceCharacter(String oldCharacter, String newCharacter, String text) {
        return text.replace(oldCharacter, newCharacter);
    }

    public static List<AccountType> accountType(ResponseData data) {
        ObjectMapper mapper = new ObjectMapper();
        return  mapper.convertValue(
                data.getUtilLoad().get(Constant.ACCOUTN_TYPE),
                new TypeReference<List<AccountType>>(){}
        );
    }

    public static String getAccountType(String accountType, ResponseData data) {
        return accountType(data).stream().filter(p -> p.getKey().equals(accountType))
                .collect(Collectors.toList()).get(0).getValue();
    }
}
