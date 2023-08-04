package co.com.bancolombia.libreinversion.model.request;

import co.com.bancolombia.libreinversion.model.customer.Customer;
import co.com.bancolombia.libreinversion.model.customer.PersonalData;
import co.com.bancolombia.libreinversion.model.document.Document;
import co.com.bancolombia.libreinversion.model.product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EnableOfferRQ {

    private String saleType;
    private String legalTrace;
    private Customer customer;
    private List<Product> products;
    private PersonalData personalData;
    private List<Document> documents;
}
