package co.com.bancolombia.libreinversion.model.stoc.gateways;

import co.com.bancolombia.libreinversion.model.stoc.GeneralInformation;
import co.com.bancolombia.libreinversion.model.stoc.OpportunitiesRQDataArgs;
import co.com.bancolombia.libreinversion.model.stoc.PersonManagementRQ;
import reactor.core.publisher.Mono;

public interface OpportunitiesGateways {

    Mono<GeneralInformation> getBusinessOportunities(OpportunitiesRQDataArgs args, String msgId);

    Mono<String> opportunitiesPersonManagement(PersonManagementRQ args, String msgId);
}
