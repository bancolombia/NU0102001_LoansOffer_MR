package co.com.bancolombia.libreinversion.usecase.test;

import co.com.bancolombia.libreinversion.model.account.rest.AccountResponse;
import co.com.bancolombia.libreinversion.model.account.rest.Balance;
import co.com.bancolombia.libreinversion.model.account.rest.DepositAccountResponse;
import co.com.bancolombia.libreinversion.model.account.rest.DepositAccountResponseData;
import co.com.bancolombia.libreinversion.model.account.rest.Office;
import co.com.bancolombia.libreinversion.model.account.rest.Property;
import co.com.bancolombia.libreinversion.model.account.rest.Regime;
import co.com.bancolombia.libreinversion.model.customer.rest.Contact;
import co.com.bancolombia.libreinversion.model.customer.rest.MetaData;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerCommercial;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerCommercialData;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerContact;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerContactData;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerDetail;
import co.com.bancolombia.libreinversion.model.customer.rest.ResponseCustomerDetailData;
import co.com.bancolombia.libreinversion.model.notification.rest.AlertIndicators;
import co.com.bancolombia.libreinversion.model.notification.rest.ResponseRetrieveInfo;
import co.com.bancolombia.libreinversion.model.notification.rest.ResponseRetrieveInfoData;

import java.util.ArrayList;
import java.util.List;

public class UtilRetrieveTestBuild {

    public static List<Property> especification() {

        List<Property> properties = new ArrayList<>();
        properties.add(Property.builder()
                .value(false)
                .build());

        properties.add(Property.builder()
                .value(false)
                .build());

        properties.add(Property.builder()
                .value(false)
                .build());

        properties.add(Property.builder()
                .value(false)
                .build());

        properties.add(Property.builder()
                .value(false)
                .build());

        properties.add(Property.builder()
                .value(false)
                .build());

        properties.add(Property.builder()
                .value(false)
                .build());

        properties.add(Property.builder()
                .value(false)
                .build());

        properties.add(Property.builder()
                .value(false)
                .build());

        properties.add(Property.builder()
                .value(false)
                .build());

        properties.add(Property.builder()
                .value(false)
                .build());

        properties.add(Property.builder()
                .value(false)
                .build());

        properties.add(Property.builder()
                .value(false)
                .build());

        properties.add(Property.builder()
                .value(false)
                .build());

        properties.add(Property.builder()
                .value(false)
                .build());

        properties.add(Property.builder()
                .value(false)
                .build());

        properties.add(Property.builder()
                .value(false)
                .build());

        properties.add(Property.builder()
                .value(false)
                .build());

        properties.add(Property.builder()
                .value(false)
                .build());

        properties.add(Property.builder()
                .value(false)
                .build());

        properties.add(Property.builder()
                .value(false)
                .build());

        properties.add(Property.builder()
                .value(false)
                .build());

        return properties;
    }

    public static DepositAccountResponse depositAccountResponse() {

        List<DepositAccountResponseData> data = new ArrayList<>();

        data.add(
                DepositAccountResponseData.builder()
                        .account(
                                AccountResponse.builder()
                                        .inactiveDays("0")
                                        .jointHolder(false)
                                        .overdueDays("0")
                                        .daysTerm("0")
                                        .regime(Regime.builder().regime("").type("").build())
                                        .balances(Balance.builder()
                                                .build())
                                        .specifications(UtilRetrieveTestBuild.especification())
                                        .office(Office.builder().code("4069").build())
                                        .build()
                        )
                        .build()
        );

        return DepositAccountResponse.builder()
                .data(data)
                .build();
    }

    public static ResponseCustomerCommercial responseCustomerCommercial() {

        return ResponseCustomerCommercial.builder()
                .data(
                        ResponseCustomerCommercialData.builder()
                                .build()
                )
                .meta(
                        MetaData.builder()
                                .build()
                )
                .build();
    }

    public static ResponseCustomerContact responseCustomerContact() {

        List<Contact> contact = new ArrayList<>();
        Contact cont = Contact.builder()
                .postalCode(null)
                .phoneNumber("7226633")
                .telephoneExtension("244")
                .authorizeReceiveOffers("true")
                .authorizeReceiveInfoViaSMS("true")
                .authorizeReceiveInfoViaEmail("true")
                .build();
        contact.add(cont);
        return ResponseCustomerContact.builder()
                .data(ResponseCustomerContactData.builder()
                        .contact(contact)
                        .build()
                )
                .meta(
                        MetaData.builder()
                                .build()
                )
                .build();
    }

    public static ResponseCustomerDetail responseCustomerDetail() {

        return ResponseCustomerDetail.builder()
                .data(
                        ResponseCustomerDetailData.builder()
                                .cityExpedition("2000-12-12T00:00:00.0")
                                .birthCity(null)
                                .profession(null)
                                .position(null)
                                .academicLevel(null)
                                .numberEmployees("0")
                                .numberChildren("0")
                                .nameCompanyWorks("")
                                .codeSubCIIU(null)
                                .entityType(null)
                                .stateEntity(null)
                                .civilSociety(null)
                                .natureEntity(null)
                                .decentralizedEntity(null)
                                .build()
                )
                .meta(
                        MetaData.builder()
                                ._requestDateTime("2021-09-20T13:51:00-05:00")
                                .build()
                )
                .build();
    }

    public static ResponseRetrieveInfo responseRetrieveInfo() {

        List<AlertIndicators> alertIndicators = new ArrayList<>();
        alertIndicators.add(AlertIndicators.builder()
                .pushActive(0)
                .lastDataModificationDate("2021-08-23")
                .build());

        return ResponseRetrieveInfo.builder()
                .data(
                        ResponseRetrieveInfoData.builder()
                                .enrollmentDate("2021-08-23")
                                .lastMechanismUpdateDate("2021-08-23")
                                .alertIndicators(alertIndicators)
                                .build()
                )
                .build();
    }
}
