package co.com.bancolombia.libreinversion.model.offer.sellutil;

import co.com.bancolombia.libreinversion.model.constants.SellConst;
import co.com.bancolombia.libreinversion.model.customer.CustomerFactory;
import co.com.bancolombia.libreinversion.model.enums.DigitSignatureEnum;
import co.com.bancolombia.libreinversion.model.enums.InsuranceTypeEnum;
import co.com.bancolombia.libreinversion.model.enums.TipoMonedaEnum;
import co.com.bancolombia.libreinversion.model.enums.TipoPagareEnum;
import co.com.bancolombia.libreinversion.model.offer.Loan;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

public abstract class SellBuildBase {

    protected static SellConst sellConst;
    protected static CustomerFactory customerFactory;
    protected static Map<String, Object> utilMAP;
    protected static DigitSignatureEnum digitSignatureEnum;
    protected static InsuranceTypeEnum insuranceTypeEnum;
    protected static TipoMonedaEnum tipoMonedaEnum;
    protected static TipoPagareEnum tipoPagareEnum;
    protected static Arrays arrays;
    protected static Optional<Loan> utilLoan;
    protected static final TechLogger log = LoggerFactory.getLog(SellBuildBase.class.getName());

    protected SellBuildBase() {
        throw new IllegalStateException("Utility class");
    }

    protected static String strDate(String formatPattern) {
        LocalDateTime localDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
        return formatter.format(localDate);
    }

    protected static String encodeBase64(byte[] array) {
        return Base64.getEncoder().encodeToString(array);
    }

}
