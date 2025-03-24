package test_components.authentication_service;

import java.io.File;
import java.util.Map;

import org.testng.annotations.Test;

import hsbc.utilities.ApiClient;
import hsbc.utilities.Headers;
import io.restassured.http.Method;
import test_components.BaseTest;

public class CorporateToSparcTest extends BaseTest {

	@Test
	public void validateStatusCode() {
		ApiClient apiClient = new ApiClient();
		Headers headers = new Headers();
		Map<String, String> headers_map = headers.corporateToSparc("application/json");
		apiClient.sendRequest(Method.POST, this.uri, null, headers_map, new File(""));
	}
}
