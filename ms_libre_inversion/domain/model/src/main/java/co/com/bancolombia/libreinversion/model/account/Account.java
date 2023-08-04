package co.com.bancolombia.libreinversion.model.account;

import co.com.bancolombia.libreinversion.model.account.rest.AccountResponse;
import co.com.bancolombia.libreinversion.model.account.rest.DepositAccountResponseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Schema(required = true)
    private String type;

    @Schema(required = true)
    private String number;

    public Account(AccountResponse response) {
        this.type = response.getType();
        this.number = response.getNumber();
    }

    public static Account buildAccount(DepositAccountResponseData response) {
        return new Account(response.getAccount());
    }

}
