package co.com.bancolombia.libreinversion.usecase.contactability;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.commons.ErrorEnum;
import co.com.bancolombia.libreinversion.model.constants.SellConst;
import co.com.bancolombia.libreinversion.model.contactability.mail.emailsms.BodyMailAttachedApiRQ;
import co.com.bancolombia.libreinversion.model.contactability.mail.emailsms.BodyMailBasicApiRQ;
import co.com.bancolombia.libreinversion.model.events.QueueMessageEmail;
import co.com.bancolombia.libreinversion.model.events.QueueMsgEmailAttached;
import co.com.bancolombia.libreinversion.model.events.gateways.EventsGateway;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ContactabilityUseCase {

    private final EventsGateway eventsGateway;
    private static final TechLogger log = LoggerFactory.getLog(ContactabilityUseCase.class.getName());

    public Mono<String> sendBasicMail(String messageId, int pripority, BodyMailBasicApiRQ reqSendMail) {

        return Mono.just("");
    }

    public Mono<String> sendAttachmentMail(String messageId, int pripority, BodyMailAttachedApiRQ reqSendMail) {
        final String method = "sendAttachmentMail";
        try {
            return Mono.just("");
        } catch (RuntimeException e) {
            return Mono.error(buildError(method, e));
        }
    }

    private LibreInversionException buildError(String method, Throwable e) {
        log.error(e);
        return new LibreInversionException(
                ErrorEnum.MSG_LI022.getMessage(), ErrorEnum.MSG_LI022.getMessage(),
                Constant.CONTACTABILITY_CLIENT_ERROR, SellConst.SELL_OFFER, method, "");
    }
}
