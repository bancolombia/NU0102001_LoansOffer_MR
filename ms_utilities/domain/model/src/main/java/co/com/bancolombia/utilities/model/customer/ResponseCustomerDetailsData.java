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
public class ResponseCustomerDetailsData {
    private String mdmKey;
    private String expeditionDate;
    private String cityExpedition;
    private String countryExpedition;
    private LocalDate birthDate;
    private String birthDepartment;
    private String birthCountry;
    private String nationality;
    private String firstName;
    private String secondName;
    private String firstSurname;
    private String shortNameClient;
    private String customerTreatment;
    private String gender;
    private String civilStatusCode;
    private String occupation;
    private String position;
    private String numberEmployees;
    private String numberChildren;
    private String codeCIIU;
    private String codeSubCIIU;
    private String sectorCode;
    private String subSectorCode;
    private String economicActivity;
}