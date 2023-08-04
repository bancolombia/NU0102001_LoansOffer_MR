package co.com.bancolombia.libreinversion.model.account;

import co.com.bancolombia.libreinversion.model.account.rest.DepositAccountResponse;
import co.com.bancolombia.libreinversion.model.account.rest.DepositAccountData;
import co.com.bancolombia.libreinversion.model.account.rest.Property;
import co.com.bancolombia.libreinversion.model.account.rest.AccountRQ;
import co.com.bancolombia.libreinversion.model.account.rest.Participant;
import co.com.bancolombia.libreinversion.model.account.rest.Identification;
import co.com.bancolombia.libreinversion.model.account.rest.AccountCustomer;
import co.com.bancolombia.libreinversion.model.account.rest.Pagination;
import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.IdTypeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.stream.Collectors;

public class AccountFactory {

    private AccountFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static Mono<DepositAccounts> buildResponse(DepositAccountResponse responseService) {

        List<Account> accountList = responseService.getData().stream().map(Account::buildAccount)
                .collect(Collectors.toList());
        return Mono.just(DepositAccounts.builder().account(accountList).build());
    }

    public static Mono<DepositAccountData> buildRequest(String idType, String idNumber, Object especification) {
        AccountRQ account = AccountRQ.builder()
                .allowCredit(Boolean.TRUE)
                .allowDebit(Boolean.TRUE)
                .participant(Participant.builder().relation(Constant.TITULAR).build())
                .specifications(mapperEspecification(especification))
                .build();
        Identification identification = Identification.builder()
                .type(IdTypeEnum.valueOfType(idType).getIdType())
                .number(padLeftZeros(idNumber, Constant.LENGTH_ACCOUNT_ID_NUMBER)).build();
        AccountCustomer customer = AccountCustomer.builder()
                .identification(identification).build();
        Pagination pagination = Pagination.builder()
                .key(Constant.PAGINATION_KEY)
                .size(Constant.PAGINATION_SIZE).build();

        return Mono.just(DepositAccountData.builder()
                .account(account)
                .customer(customer)
                .pagination(pagination).build());
    }

    public static String padLeftZeros(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);

        return sb.toString();
    }

    private static List<Property> mapperEspecification(Object especification) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(
                especification,
                new TypeReference<List<Property>>() {
                }
        );
    }
}
