package sg.edu.nus.ssf_assessment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import sg.edu.nus.ssf_assessment.model.Quotation;
import sg.edu.nus.ssf_assessment.service.QuotationService;

@SpringBootTest
@AutoConfigureMockMvc
class SsfAssessmentApplicationTests {
	@Autowired
	private MockMvc mvc;

	@Autowired
	private QuotationService quoteSvc;

	@Test
	void contextLoads() {
		List<String> items = new ArrayList<>();
		items.add("durian");
		items.add("plum");
		items.add("pear");

		Optional<Quotation> opt = quoteSvc.getQuotations(items);
		assertTrue(opt.isEmpty());
	}

	@Test
	void testGetQuotations() throws Exception {
		JsonObject payload = Json.createObjectBuilder()
				.add("name", "Test")
				.add("address", "Blk 123")
				.add("email", "test@gmail.com")
				.add("lineItems",
						Json.createArrayBuilder()
								.add(Json.createObjectBuilder().add("item", "durian").add("quantity", 3))
								.add(Json.createObjectBuilder().add("item", "plum").add("quantity", 1))
								.add(Json.createObjectBuilder().add("item", "pear").add("quantity", 2)))
				.add("navigationId", 20).build();
		RequestBuilder req = MockMvcRequestBuilders.post("/api/po")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload.toString());
		System.out.println(payload.toString());
		MvcResult result = mvc.perform(req).andReturn();
		int status = result.getResponse().getStatus();
		String respPayload = result.getResponse().getContentAsString();

		assertEquals(404, status);
		System.out.println(respPayload);
	}
}
