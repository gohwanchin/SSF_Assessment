package sg.edu.nus.ssf_assessment.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import sg.edu.nus.ssf_assessment.service.QuotationService;

@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class PurchaseOrderRestController {
    @Autowired
    private QuotationService quoteSvc;

    @PostMapping("/po")
    public ResponseEntity<String> postPo(@RequestBody String payload) {
        Optional<JsonObject> opt = quoteSvc.calculateCost(payload);
        // If quotation is null, returns error 404 not found
        if (opt.isEmpty())
            return ResponseEntity.status(404).body(Json.createObjectBuilder().build().toString());
        return ResponseEntity.ok(opt.get().toString());
    }
}
