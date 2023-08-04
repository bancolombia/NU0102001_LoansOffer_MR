package co.com.bancolombia.libreinversion.usecase.soap;

import co.com.bancolombia.libreinversion.model.commons.Constant;
import co.com.bancolombia.libreinversion.model.constants.SellConst;
import co.com.bancolombia.libreinversion.model.exception.LibreInversionException;
import co.com.bancolombia.libreinversion.model.product.Attribute;
import co.com.bancolombia.libreinversion.model.product.RuleRequest;
import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import co.com.bancolombia.libreinversion.model.product.gateways.MAPGateways;
import co.com.bancolombia.libreinversion.model.request.ConfirmOfferComplete;
import co.com.bancolombia.libreinversion.model.request.gateways.RedisGateways;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.common.header.RequestHeader;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.gateway.GestionarPagareElectV1Gateway;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.creategiradorpn.CrearGiradorPN;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.createpay.CreatePay;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.request.signaturepay.FirmarPagare;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.response.creategiradorpn.CrearGiradorPNResponse;
import co.com.bancolombia.libreinversion.model.soap.gestionarpagareelectronico.response.createpay.CrearPagareResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@RequiredArgsConstructor
public class GestionarPagareElectUseCase {

    private final GestionarPagareElectV1Gateway gestionarPagareElectronico;
    private final MAPGateways mapGateways;
    private final RedisGateways redisGateways;


    public Mono<CrearPagareResponse> crearPagare(CreatePay requestPay, RequestHeader header) {
        return gestionarPagareElectronico.crearPagare(requestPay, header);
    }

    public Mono<String> firmaPagare(FirmarPagare reqFirmaPagare, RequestHeader header) {
        return gestionarPagareElectronico.firmaPagare(reqFirmaPagare, header);
    }

    public Mono<CrearGiradorPNResponse> crearGiradorPN(CrearGiradorPN reqCrearGiradorPN, RequestHeader header) {
        return gestionarPagareElectronico.crearGiradorPN(reqCrearGiradorPN, header);
    }

    public Mono<RuleResponse> queryRuleParamDeceval() {
        RuleRequest rule = RuleRequest.builder()
                .ruleName(SellConst.MAP_DECEVAL_PARAM)
                .productCode(SellConst.MAP_PRODUCT_CODE)
                .attributes(Arrays.asList(Attribute.builder()
                        .name(SellConst.MAP_DECEVAL_PARAM)
                        .value(SellConst.MAP_DECEVAL_PARAM).build()))
                .build();
        return mapGateways.ruleValidate(rule);
    }

    public Mono<RuleResponse> queryRuleParamSellOffer() {
        RuleRequest rule = RuleRequest.builder()
                .ruleName(SellConst.MAP_SELL_OFFER_PARAM)
                .productCode(SellConst.MAP_PRODUCT_CODE)
                .attributes(Arrays.asList(Attribute.builder()
                        .name(SellConst.MAP_SELL_OFFER_PARAM)
                        .value(SellConst.MAP_SELL_OFFER_PARAM).build()))
                .build();
        return mapGateways.ruleValidate(rule);
    }

    public Mono<ConfirmOfferComplete> getCompleteDataCache(String idCustomer) {
        return redisGateways.getCompleteDataFromCache(idCustomer).flatMap(confirmOfferComp -> {
            if (confirmOfferComp == null
                    || confirmOfferComp.getResponseCustomerDetail() == null
                    || confirmOfferComp.getResponseCustomerContact() == null
                    || confirmOfferComp.getResponseRetrieveInfo() == null
                    || confirmOfferComp.getConfirmOfferRQ() == null) {
                return Mono.error(new LibreInversionException(Constant.MDM_CLIENT_NOT_FOUND,
                        Constant.ERROR_LI001, Constant.MDM_CLIENT_NOT_FOUND,
                        SellConst.SELL_OFFER, SellConst.SELL_OFFER, ""));
            }
            return Mono.just(confirmOfferComp);
        });
    }

}
