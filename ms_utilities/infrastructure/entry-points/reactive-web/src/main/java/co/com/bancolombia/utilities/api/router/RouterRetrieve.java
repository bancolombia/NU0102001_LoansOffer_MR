package co.com.bancolombia.utilities.api.router;

import co.com.bancolombia.utilities.api.handler.HandlerRetrieve;
import co.com.bancolombia.utilities.model.RequestAmortization;
import co.com.bancolombia.utilities.model.ResponseAmortization;
import co.com.bancolombia.utilities.model.installments.Error;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRetrieve {

    @Value("${routes.basePath}")
    private String basePath;

    @Value("${routes.retrieve}")
    private String route;

    @Bean
    @RouterOperation(
            beanClass = HandlerRetrieve.class,
            path = "/retrieve",
            method = RequestMethod.POST,
            beanMethod = "listenRetrieve",
            operation = @Operation(
                    operationId = "listenRetrieve",
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "successful operation",
                                    content = @Content(examples = @ExampleObject(
                                            value = "{\n" +
                                                    "    \"data\": {\n" +
                                                    "        \"customer\": {\n" +
                                                    "            \"identification\": {\n" +
                                                    "                \"type\": \"TIPDOC_FS001\",\n" +
                                                    "                \"number\": \"2101068007\"\n" +
                                                    "            },\n" +
                                                    "            \"companyIdType\": null,\n" +
                                                    "            \"companyIdNumber\": null,\n" +
                                                    "            \"customerReliability\": \"G2\"\n" +
                                                    "        },\n" +
                                                    "        \"installmentsData\": {\n" +
                                                    "            \"installmentData\": [\n" +
                                                    "                {\n" +
                                                    "                    \"installment\": 505947,\n" +
                                                    "                    \"paymentDay\": null,\n" +
                                                    "                    \"interestRate\": null,\n" +
                                                    "                    \"monthOverdueInterestRate\": 1.3699,\n" +
                                                    "                    \"arreasInterestRate\": 0.164388,\n" +
                                                    "                    \"effectiveAnnualInterestRate\": 0.177357,\n" +
                                                    "                    \"nominalAnnualInterestRate\": 0.164388,\n" +
                                                    "                    \"interestRateType\": \"F\",\n" +
                                                    "                    \"variableInterestRateAdditionalPoints\": 0.0,\n" +
                                                    "                    \"expirationDate\": null,\n" +
                                                    "                    \"insurances\": {\n" +
                                                    "                        \"insurance\": [\n" +
                                                    "                            {\n" +
                                                    "                                \"type\": \"SV\",\n" +
                                                    "                                \"amount\": 21750\n" +
                                                    "                            }\n" +
                                                    "                        ]\n" +
                                                    "                    },\n" +
                                                    "                    \"availabilityHandlingFee\": null,\n" +
                                                    "                    \"fng\": null,\n" +
                                                    "                    \"amortizationSchedule\": null\n" +
                                                    "                },\n" +
                                                    "                {\n" +
                                                    "                    \"installment\": 548067,\n" +
                                                    "                    \"paymentDay\": null,\n" +
                                                    "                    \"interestRate\": null,\n" +
                                                    "                    \"monthOverdueInterestRate\": 1.3199,\n" +
                                                    "                    \"arreasInterestRate\": 0.158388,\n" +
                                                    "                    \"effectiveAnnualInterestRate\": 0.170407,\n" +
                                                    "                    \"nominalAnnualInterestRate\": 0.158388,\n" +
                                                    "                    \"interestRateType\": \"F\",\n" +
                                                    "                    \"variableInterestRateAdditionalPoints\": 0.0,\n" +
                                                    "                    \"expirationDate\": null,\n" +
                                                    "                    \"insurances\": {\n" +
                                                    "                        \"insurance\": [\n" +
                                                    "                            {\n" +
                                                    "                                \"type\": \"SV\",\n" +
                                                    "                                \"amount\": 21750\n" +
                                                    "                            },\n" +
                                                    "                            {\n" +
                                                    "                                \"type\": \"SD\",\n" +
                                                    "                                \"amount\": 42120\n" +
                                                    "                            }\n" +
                                                    "                        ]\n" +
                                                    "                    },\n" +
                                                    "                    \"availabilityHandlingFee\": null,\n" +
                                                    "                    \"fng\": null,\n" +
                                                    "                    \"amortizationSchedule\": null\n" +
                                                    "                }\n" +
                                                    "            ]\n" +
                                                    "        }\n" +
                                                    "    }\n" +
                                                    "}"),
                                            schema = @Schema(implementation = ResponseAmortization.class))
                            ),
                            @ApiResponse(
                                    responseCode = "404",
                                    description = "Bad request",
                                    content = @Content(schema = @Schema(implementation = Error.class))
                            ),
                            @ApiResponse(
                                    responseCode = "500",
                                    description = "Internal server error",
                                    content = @Content(schema = @Schema(implementation = Error.class))
                            )
                    },
                    requestBody = @RequestBody(
                            required = true,
                            content = @Content(examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"data\": {\n" +
                                            "        \"customer\": {\n" +
                                            "            \"identification\": {\n" +
                                            "                \"type\": \"TIPDOC_FS001\",\n" +
                                            "                \"number\": \"2101068007\"\n" +
                                            "            },            \n" +
                                            "            \"customerReliability\": \"G2\"\n" +
                                            "        },\n" +
                                            "        \"offer\": {\n" +
                                            "            \"productId\": \"14\",\n" +
                                            "            \"amount\": 15000000,\n" +
                                            "            \"interestRateType\": \"F\",\n" +
                                            "            \"term\": 48,\n" +
                                            "            \"insurances\": {\n" +
                                            "                \"insurance\": [\n" +
                                            "                    {\n" +
                                            "                        \"type\": \"SV\"\n" +
                                            "                    },\n" +
                                            "                    {\n" +
                                            "                        \"type\": \"SD\"\n" +
                                            "                    }\n" +
                                            "                ]\n" +
                                            "            },\n" +
                                            "            \"amortizationSchedule\": false\n" +
                                            "        }\n" +
                                            "    }\n" +
                                            "}"),
                                    schema = @Schema(implementation = RequestAmortization.class)
                            )
                    )
            )
    )
    public RouterFunction<ServerResponse> routerRetrieveUtilities(HandlerRetrieve handlerRetrieve) {
        return route(POST(basePath.concat(route)), handlerRetrieve::listenRetrieve);
    }
}
