package co.com.bancolombia.libreinversion.model.customer;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.commons.IdTypeEnum;
import co.com.bancolombia.libreinversion.model.enums.DisbursementAccountEnum;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerDetail;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerContact;
import co.com.bancolombia.libreinversion.model.customer.rest.RequestBodyData;
import co.com.bancolombia.libreinversion.model.customer.rest.CustomerRQ;
import co.com.bancolombia.libreinversion.model.customer.rest.Contact;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerCommercial;

import co.com.bancolombia.libreinversion.model.notification.rest.AlertIndicators;
import co.com.bancolombia.libreinversion.model.notification.rest.ResponseRetrieveInfo;
import co.com.bancolombia.libreinversion.model.notification.rest.RetrieveInformationRQ;
import co.com.bancolombia.libreinversion.model.notification.rest.CustomerIdentificationRQ;
import co.com.bancolombia.libreinversion.model.notification.rest.CustomerIdentification;

import co.com.bancolombia.libreinversion.model.product.Product;
import co.com.bancolombia.libreinversion.model.request.RetrieveOfferRQ;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple4;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

public class CustomerFactory {

    private static final TechLogger log = LoggerFactory.getLog(CustomerFactory.class.getName());

    private CustomerFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static String getNames(ResponseCustomerDetail obj) {
        if (obj.getData().getSecondName() == null || "".equals(obj.getData().getSecondName())) {
            return obj.getData().getFirstName();
        } else {
            return obj.getData().getFirstName().concat(" ").concat(obj.getData().getSecondName());
        }
    }

    public static String getSurnames(ResponseCustomerDetail obj) {
        if (obj.getData().getSecondSurname() == null || "".equals(obj.getData().getSecondSurname())) {
            return obj.getData().getFirstSurname();
        } else {
            return obj.getData().getFirstSurname().concat(" ").concat(obj.getData().getSecondSurname());
        }
    }

    public static String getMobileNumber(ResponseCustomerContact obj, ResponseRetrieveInfo obj2) {
        Optional<AlertIndicators> optional = obj2.getData().getAlertIndicators().stream().findFirst();
        Optional<Contact> value = obj.getData().getContact().stream().findFirst();
        String customerMobileNumber = "";

        try {
            if (optional.isPresent()) {
                customerMobileNumber = optional.get().getCustomerMobileNumber();
            } else {
                if (value.isPresent()) {
                    customerMobileNumber = value.get().getMobilPhone();
                }
            }
        } catch (RuntimeException e) {
            customerMobileNumber = "";
            log.error("getMobileNumber :" + e.getMessage());
        } finally {
            if (value.isPresent() && customerMobileNumber == null) {
                customerMobileNumber = value.get().getMobilPhone();
            }
        }

        return customerMobileNumber;
    }

    public static String getAddress(ResponseCustomerContact obj) {

        String address = "";
        Optional<Contact> value = obj.getData().getContact().stream().findFirst();
        if (value.isPresent()) {
            address = value.get().getAddress();
        }

        return address;
    }

    public static String getEmail(ResponseCustomerContact obj, ResponseRetrieveInfo obj2) {
        String email = "";
        Optional<AlertIndicators> optional = obj2.getData().getAlertIndicators().stream().findFirst();
        Optional<Contact> value = obj.getData().getContact().stream().findFirst();

        if (optional.isPresent()) {
            if (optional.get().getCustomerEmail() != null) {
                email = optional.get().getCustomerEmail();
            } else {
                if (value.isPresent()) {
                    email = value.get().getEmail();
                }
            }
        } else {
            if (value.isPresent()) {
                email = value.get().getEmail();
            }
        }
        return email;
    }

    public static String getCityCode(ResponseCustomerContact obj) {
        String cityCode = "";
        Optional<Contact> value = obj.getData().getContact().stream().findFirst();
        if (value.isPresent()) {
            cityCode = value.get().getCityCode();
        }
        return cityCode;
    }

    public static Mono<Integer> getAge(String birthDate) {
        return Mono.just(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S"))
                .map(formatter -> Period.between(LocalDate.parse(birthDate, formatter),
                        LocalDate.now()).getYears());
    }

    public static RequestBodyData getRqCustomer(String idType, String idNumber) {
        CustomerRQ rqBody = CustomerRQ.builder()
                .customerDocumentType(idType)
                .customerDocumentId(idNumber).build();
        return RequestBodyData.builder().data(rqBody).build();
    }

    public static RetrieveInformationRQ getRqAlert(String idType, String idNumber) {
        CustomerIdentification ci = CustomerIdentification.builder()
                .documentNumber(idNumber)
                .documentType(IdTypeEnum.valueOfType(idType).getIdType())
                .build();
        return RetrieveInformationRQ.builder()
                .data(CustomerIdentificationRQ.builder()
                        .customerIdentification(ci).build())
                .build();
    }

    public static Mono<Object> validateContactData(ResponseCustomerContact contact,
                                                   ResponseRetrieveInfo alert, String msgId) {
        return Mono.just(CustomerFactory.getMobileNumber(contact, alert))
                .filter(num -> alertExists(num, CustomerFactory.getEmail(contact, alert)))
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new LibreInversionException(ErrorEnum.MSG_LI005.getName(),
                                ErrorEnum.MSG_LI005.getName(), ErrorEnum.MSG_LI005.getMessage(), "", "", msgId))))
                .flatMap(conta -> Mono.just(contact));
    }

    private static Boolean alertExists(String num, String email) {
        return ((Objects.nonNull(num) && !"".equals(num.trim())) ||
                (Objects.nonNull(email) && !"".equals(email.trim())));
    }

    public static String existsCellphone(ResponseRetrieveInfo t1, ResponseCustomerContact t2) {

        String cellphoneOne = null;
        String cellphoneTwo = null;
        Optional<AlertIndicators> opCell = t1.getData().getAlertIndicators().stream().findFirst();
        if (opCell.isPresent()) {
            cellphoneOne = opCell.get().getCustomerMobileNumber();
        }

        Optional<Contact> opCellTwo = t2.getData().getContact().stream().findFirst();
        if (opCellTwo.isPresent()) {
            cellphoneTwo = opCellTwo.get().getMobilPhone();
        }

        boolean res = false;

        if (cellphoneOne != null || cellphoneTwo != null) {
            res = true;
        }

        return res ? "verdadero" : "falso";
    }

    public static String existsEmail(ResponseRetrieveInfo t1, ResponseCustomerContact t2) {
        String emailOne = null;
        String emailTwo = null;

        Optional<AlertIndicators> opEmailOne = t1.getData().getAlertIndicators().stream().findFirst();
        Optional<Contact> opEmailTwo = t2.getData().getContact().stream().findFirst();

        if (opEmailOne.isPresent()) {
            emailOne = opEmailOne.get().getCustomerEmail();
        }

        if (opEmailTwo.isPresent()) {
            emailTwo = opEmailTwo.get().getEmail();
        }

        boolean res = false;

        if (emailOne != null || emailTwo != null) {
            res = true;
        }

        return res ? "verdadero" : "falso";
    }

    public static String getProduct(RetrieveOfferRQ request) {
        Optional<Product> op = request.getProducts().stream().findFirst();
        if (op.isPresent()) {
            return op.get().getId();
        }
        return Constant.PRODUCT_ID;
    }

    public static String getBirthDate(ResponseCustomerDetail obj) {
        String outputString = null;
        try {
            if (obj.getData() == null && obj.getData().getBirthDate() == null) {
                return outputString;
            } else {
                if (obj.getData().getBirthDate() != null) {
                    DateFormat inputFormat = new SimpleDateFormat(Constant.DATE_FORMAT);
                    Date date = inputFormat.parse(obj.getData().getBirthDate());
                    DateFormat outputFormat = new SimpleDateFormat(Constant.DATE_FORMAT);
                    outputString = outputFormat.format(date);
                }
            }
        } catch (ParseException e) {
            log.error("getBirthDate: " + e.getMessage());
        }
        return outputString;
    }

    public static String getDocumentType(String valid) {
        String type = valid;

        if (valid != null && valid.length() > Constant.NUMBER_DOCUMENT) {
            String[] arrValid = valid.split("_");
            type = arrValid[1];
        }

        return type;
    }

    public static String getCountryCode(String valid) {
        String type = valid;
        if (valid != null && valid.length() > Constant.NUMBER_TWO) {
            String[] arrValid = valid.split("_");
            type = arrValid[1];
        }
        return type;
    }

    public static String getDepartamentCode(String valid) {
        String type = valid;
        if (valid != null && valid.length() > 3) {
            String[] arrValid = valid.split("_");
            int numCity = Integer.parseInt(arrValid[Constant.NUMBER_TWO]);
            type = Integer.toString(numCity).length() == 1 ? ("0" + numCity) : Integer.toString(numCity);
        }
        return type;
    }

    public static String getCityParentCode(String valid) {
        String type = valid;
        try {
            if (valid != null && valid.length() > Constant.NUMBER_FIVE) {
                String[] arrValid = valid.split("_");
                String numStr = (arrValid[1].substring(0, Constant.NUMBER_FIVE)) + "000";
                int numCity = Integer.parseInt(numStr);
                type = Integer.toString(numCity);
            }
        } catch (IndexOutOfBoundsException e) {
            log.error("getCityParentCode: " + e.getMessage());
        }
        return type;
    }

    public static DisbursementAccountEnum getDisbursementAccountType(String type) {
        DisbursementAccountEnum typeAccount = null;
        if (type.trim().toUpperCase().contains("AHORRO")) {
            typeAccount = DisbursementAccountEnum.AHO;
        } else if (type.trim().toUpperCase().contains("CORRIENTE")) {
            typeAccount = DisbursementAccountEnum.CTE;
        } else if (type.trim().toUpperCase().contains("CHEQUE")) {
            typeAccount = DisbursementAccountEnum.CHQ;
        }
        return typeAccount;
    }

    public static Mono<Tuple4<ResponseCustomerCommercial,
            ResponseCustomerContact,
            ResponseCustomerDetail,
            ResponseRetrieveInfo>> validateAlertIndicators(Tuple4<ResponseCustomerCommercial,
            ResponseCustomerContact,
            ResponseCustomerDetail,
            ResponseRetrieveInfo> tuple, String msgId) {

        if (tuple.getT4().getData().getDynamicKeyMechanism() == null) {
            return Mono.error(new LibreInversionException(ErrorEnum.MSG_OR001.getName(),
                    ErrorEnum.MSG_OR001.getName(), ErrorEnum.MSG_OR001.getMessage(), "", "", msgId));
        }
        return Mono.just(tuple);
    }
}
