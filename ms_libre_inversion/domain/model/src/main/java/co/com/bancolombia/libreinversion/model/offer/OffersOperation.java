package co.com.bancolombia.libreinversion.model.offer;


import co.com.bancolombia.libreinversion.model.request.ConfirmOfferRQ;
import co.com.bancolombia.libreinversion.model.request.SellOfferRQ;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class OffersOperation {

    private EnableOfferData enable;
    private ConfirmOfferRQ confirm;
    private SellOfferRQ sell;
}
