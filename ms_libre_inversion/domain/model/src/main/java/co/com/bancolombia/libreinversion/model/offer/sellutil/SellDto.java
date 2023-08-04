package co.com.bancolombia.libreinversion.model.offer.sellutil;


import co.com.bancolombia.libreinversion.model.document.InfoDocument;
import co.com.bancolombia.libreinversion.model.request.Insurances;
import co.com.bancolombia.libreinversion.model.product.RuleResponse;
import co.com.bancolombia.libreinversion.model.rate.RangeType;
import co.com.bancolombia.libreinversion.model.request.SellOfferRQ;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.ToString;

import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder(toBuilder = true)
public class SellDto {
    private String messageId;
    private String token;
    private Long timeCache;
    private Long timeUrlPreSigned;
    private SellOfferRQ sellOfferRQ;
    private Map<String, InfoDocument> docLiNativo;
    private Optional<Insurances> employment;
    private RuleResponse ruleDeceval;
    private RuleResponse paramSellOffer;
    private RuleResponse paramDocumentManage;
    private InfoDocument employmentDocMerge;
    private InfoDocument mergeDocLiNativo;
    private RangeType rangeType;
    private CacheSell cacheSell;
    private String creditPlan;
}
