package sg.edu.nus.ssf_assessment.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import sg.edu.nus.ssf_assessment.model.Quotation;

@Service
public class QuotationService {
    private static final String URL = "https://quotation.chuklee.com/quotation";

    public Optional<JsonObject> calculateCost(String json) {
        System.out.println(">>>>>>>>Request received:" + json);
        DocumentContext jsonContext = JsonPath.parse(json);
        List<Map<String, Object>> list = jsonContext.read("$.lineItems");
        List<String> itemList = list.stream().map(m -> (String) m.get("item")).toList();
        Optional<Quotation> opt = getQuotations(itemList);
        if (opt.isEmpty())
            return Optional.empty();
        // Calculate total cost
        System.out.println(">>>>>>>>Calculating cost");
        Quotation q = opt.get();
        double total = 0.0;
        for (Map<String, Object> m : list) {
            String item = (String) m.get("item");
            int qty = (Integer) m.get("quantity");
            Float price = q.getQuotation(item);
            total += price * qty;
        }
        JsonObject o = Json.createObjectBuilder().add("invoiceId", q.getQuoteId())
                .add("name", (String) jsonContext.read("$.name"))
                .add("total", total).build();
        return Optional.of(o);
    }

    public Optional<Quotation> getQuotations(List<String> items) {
        JsonArrayBuilder ab = Json.createArrayBuilder();
        for (String i : items)
            ab.add(i);
        JsonArray arr = ab.build();
        // Make HTTP call to QSys
        RequestEntity<String> req = RequestEntity.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(arr.toString(), String.class);
        RestTemplate template = new RestTemplate();
        try {
            ResponseEntity<String> resp = template.exchange(req, String.class);
            System.out.println(">>>>>>>>API response:" + resp.getBody());
            // Marshal Json object into Quotation object
            Quotation quote = new Quotation();
            DocumentContext jsonContext = JsonPath.parse(resp.getBody());
            quote.setQuoteId(jsonContext.read("$.quoteId"));
            List<Map<String, Object>> priceList = jsonContext.read("$.quotations");
            Map<String, Float> quotations = new HashMap<>();
            for (Map<String, Object> m : priceList) {
                String item = (String) m.get("item");
                Float price = ((Number) m.get("unitPrice")).floatValue();
                quotations.put(item, price);
            }
            quote.setQuotations(quotations);
            return Optional.of(quote);
        } catch (Exception e) {
            System.out.println(">>>>>>>>API request failed");
            return Optional.empty();
        }
    }
}
