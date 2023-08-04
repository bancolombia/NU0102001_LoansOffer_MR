package co.com.bancolombia.libreinversion.model.offer.sellutil;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.constants.SellConst;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.model.request.DisbursementDestination;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;

public class UtilSell {

    private static final TechLogger log = LoggerFactory.getLog(UtilSell.class.getName());

    private UtilSell() {
        throw new IllegalStateException("Utility class");
    }


    public static String validObjectRuleNull(Object value) {
        return value == null ? null : value.toString();
    }

    public static LibreInversionException buildErr(String message, String msgId) {
        return new LibreInversionException(
                Constant.AWS_S3_CLIENT_ERROR, Constant.ERROR_LI001,
                message, SellConst.SELL_OFFER, SellConst.SELL_OFFER, msgId);
    }

    public static String buildMsgErrS3DocNotFound(Throwable e, String nameFile) {
        String msg = SellConst.ERR_DOCS_S3 + nameFile;
        if (e != null) {
            if (e.getMessage().toLowerCase().contains(SellConst.ERR_KEY_S3_NOT_FOUND.toLowerCase())) {
                msg = SellConst.ERR_DOCS_S3_NOT_FOUND + nameFile;
            } else if (e.getMessage().toLowerCase().contains(SellConst.ERR_BUCKET_S3_NOT_FOUND.toLowerCase())) {
                msg = Constant.AWS_S3_CLIENT_ERROR + SellConst.ERR_REPO_S3_NOT_FOUND;
            }
            msg = e.getMessage();
        }
        return msg;
    }

    public static LibreInversionException buildMsgErrProcessDco(String msgId) {
        return new LibreInversionException(
                ErrorEnum.MSG_LI020.getMessage(), Constant.ERROR_LI001,
                ErrorEnum.MSG_LI020.getMessage(), SellConst.SELL_OFFER, SellConst.SELL_OFFER, msgId);
    }

    public static LibreInversionException dataClientErrorProcess(String operation, String msgId, Throwable e) {
        String msg = ErrorEnum.MSG_LI021.getMessage();
        if (e != null) {
            log.error(e);
            msg = e.getMessage();
        }
        return new LibreInversionException(ErrorEnum.MSG_LI021.getMessage(), ErrorEnum.MSG_LI021.getName(),
                msg, SellConst.SELL_OFFER, operation, msgId);
    }

    public static LibreInversionException errorProcess(String operation, String msgId, Throwable e) {
        String msg = ErrorEnum.MSG_LI021.getMessage();
        if (e != null) {
            log.error(e);
            msg = e.getMessage();
        }
        return new LibreInversionException(ErrorEnum.MSG_LI017.getMessage(), ErrorEnum.MSG_LI017.getName(),
                msg, SellConst.SELL_OFFER, operation, msgId);
    }

    public static LibreInversionException errorInteresRate(String msg, String msgId) {
        return new LibreInversionException(msg, "001", msg, SellConst.SELL_OFFER, SellConst.SELL_OFFER, msgId);
    }

    public static boolean validateAmmount(DisbursementDestination destination, Double totalAmmoun) {
        boolean cumple = true;
        long sumAmmount = destination.getDestination()
                .stream().mapToLong(amo -> (long) amo.getAmount()).sum();
        if (sumAmmount > totalAmmoun || sumAmmount < totalAmmoun) {
            cumple = false;
        }
        return cumple;
    }
}
