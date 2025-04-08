package com.tacton.services.cpq;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductService {

	@Value("${cpq_api_url}")
	private String cpq_api_url;

	@Value("${cpq_user}")
	private String cpq_user;

	@Value("${cpq_pass}")
	private String cpq_pass;

	private static String PRODUCT_URI = "/product";

	public String getProductNameFromCPQApi(String productId) {
		final HttpHeaders headers = getAuthenticationHeaders(cpq_user, cpq_pass);

		final HttpEntity<String> httpEntity = new HttpEntity<>("body", headers);
		final RestTemplate template = new RestTemplate();

		final String url = cpq_api_url + PRODUCT_URI + "/" + productId;
		final ResponseEntity<String> response = template.exchange(
				url,
				HttpMethod.GET,
				httpEntity,
				String.class);

		return extractProductName(response);
	}

	private HttpHeaders getAuthenticationHeaders(String username, String password) {
		final String auth = username + ":" + password;
		final String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
		final String authHeader = "Basic " + new String(encodedAuth);
		final HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", authHeader);
		return headers;
	}

	private String extractProductName(ResponseEntity<String> response) {
		final Document doc = Jsoup.parse(response.getBody());

		final Element element = doc.selectFirst("attribute[name=name]");

		return element != null ? element.attr("value") : null;
	}
}
