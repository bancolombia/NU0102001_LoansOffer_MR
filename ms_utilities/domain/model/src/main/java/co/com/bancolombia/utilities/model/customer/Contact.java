package co.com.bancolombia.utilities.model.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Contact {
    private String mdmKey;
    private String addressClass;
    private String addressType;
    private LocalDate dateLastUpdate;
    private String address;
    private String cityCode;
    private String departmentCode;
    private String countryCode;
    private String postalCode;
    private String email;
    private String phoneNumber;
    private String telephoneExtension;
    private String mobilPhone;
    private String authorizeReceiveOffers;
    private String authorizeReceiveInfoViaSMS;
    private String authorizeReceiveInfoViaEmail;
}