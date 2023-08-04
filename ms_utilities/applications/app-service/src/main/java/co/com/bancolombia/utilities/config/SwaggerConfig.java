package co.com.bancolombia.utilities.config;

import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi employeeGroupApi() {
        return GroupedOpenApi.builder()
                .group("Offer")
                .addOpenApiCustomiser(getOpenApiCustomiser())
                .build();
    }

    public OpenApiCustomiser getOpenApiCustomiser() {

        return openAPI -> openAPI.getPaths().values().stream().flatMap(pathItem ->
                pathItem.readOperations().stream())
                .forEach(operation -> {
                    operation.addParametersItem(new Parameter()
                            .name("Message-Id")
                            .description("Identificador del mensaje o de correlación y se usa para trazabilidad. Este identificador permite identificar los logs de los llamados a las APIs en los repositorios de logs, además, permite emparejar la solicitud que se realiza a la API con su respuesta. Se recomienda enviar un UUID para este valor. Ejemplo: c4e6bd04-5149-11e7-b114-b2f933d5fe66.")
                            .in("header")
                            .schema(new StringSchema()
                                    .example("Message-Id"))
                            .required(true));
                    operation.addParametersItem(new Parameter()
                            .name("id")
                            .description("Message ID de la transacción – Dato de trazabilidad de la experiencia.")
                            .in("header")
                            .schema(new StringSchema()
                                    .example("id"))
                            .required(true));
                    operation.addParametersItem(new Parameter()
                            .name("deviceId")
                            .description("Identificador del dispositivo que realiza la consulta.")
                            .in("header")
                            .schema(new StringSchema()
                                    .example("deviceId"))
                            .required(true));
                    operation.addParametersItem(new Parameter()
                            .name("token")
                            .description("Token de sesión entregado por oAuth.")
                            .in("header")
                            .schema(new StringSchema()
                                    .example("token"))
                            .required(true));
                    operation.addParametersItem(new Parameter()
                            .name("timestamp")
                            .description("Timestamp")
                            .in("header")
                            .schema(new StringSchema()
                                    .example("timestamp"))
                            .required(true));
                    operation.addParametersItem(new Parameter()
                            .name("consumer")
                            .description("Codigo del consumidor. Ejemplo: APP, SVP, MOVILIDAD, etc.")
                            .in("header")
                            .schema(new StringSchema()
                                    .example("consumer"))
                            .required(true));
                });
    }
}
