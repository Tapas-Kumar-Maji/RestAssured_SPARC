package test_components.authentication_service;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import test_components.BaseTest;
import utilities.Headers;
import utilities.logging.ApiLogger;
import utilities.reporting.AssertionUtils;
import utilities.requests.PostRequest;

public class DummyApiTest extends BaseTest {

	@Test(enabled = false)
	public void addPlaceApiTest() {

		RestAssured.baseURI = this.uri;
		RestAssured.filters(new ApiLogger());
		File addPlaceAPI_requestBody = new File(
				System.getProperty("user.dir") + "/src/test/resources/DummyApi/AddPlaceAPI_RequestBody.json");
		Headers headers = new Headers();

//		Is APIClient class necessary?
//		Doing without API client
		given().queryParam("key", "qaclick123").headers(headers.addPlaceApi("application/json"))
				.body(addPlaceAPI_requestBody).post("/maps/api/place/add/json").then().statusCode(200)
				.body("scope", equalTo("APP"));
	}

	@Test
	public void addPlaceApiTest_withFramework() {

		File addPlaceAPI_requestBody = new File(
				System.getProperty("user.dir") + "/src/test/resources/DummyApi/AddPlaceAPI_RequestBody.json");

		Headers headers = new Headers();
		Map<String, Object> headers_map_dummy = headers.addPlaceApi("application/json");
		PostRequest postRequest = new PostRequest();
		postRequest.setRequestDetails(this.uri, null, null, headers_map_dummy, headers_map_dummy, new HashMap<>(),
				new HashMap<>(), addPlaceAPI_requestBody);
//		postRequest.setRequestDetails(this.uri, "/maps/api/place/add/json", new HashMap<>(), new HashMap<>(),
//				headers_map_dummy, addPlaceAPI_requestBody);
	}

	@Test(enabled = false)
	public void addPlaceApiTest_withFramework2() {

		File addPlaceAPI_requestBody = new File(
				System.getProperty("user.dir") + "/src/test/resources/DummyApi/AddPlaceAPI_RequestBody.json");

		Headers headers = new Headers();
		Map<String, Object> headers_map_dummy = headers.addPlaceApi("application/json");
		PostRequest postRequest = new PostRequest();
		postRequest.setRequestDetails(this.uri, "/maps/api/place/add/json", new HashMap<>(), new HashMap<>(),
				new HashMap<>(), new HashMap<>(), headers_map_dummy, addPlaceAPI_requestBody);
	}

	@Test
	public void addPlaceApiTest3() {

		File addPlaceAPI_requestBody = new File(
				System.getProperty("user.dir") + "/src/test/resources/DummyApi/AddPlaceAPI_RequestBody.json");

		Headers headers = new Headers();
		Map<String, Object> headers_map_dummy = headers.addPlaceApi("application/json");
		PostRequest postRequest = new PostRequest();
		Response response = postRequest.setRequestDetails(this.uri, "/maps/api/place/add/json", new HashMap<>(),
				new HashMap<>(), headers_map_dummy, new HashMap<>(), new HashMap<>(), addPlaceAPI_requestBody);

//		if (response.jsonPath().get("scope") != null) {
//			System.out.println("Scope : " + response.jsonPath().getString("scope"));
//		} else {
//			System.out.println("Field not found");
//		}

		Map<String, Object> expectedValuesMap = new HashMap<>();
		expectedValuesMap.put("status", "OK");
		expectedValuesMap.put("scope", "APP");

		AssertionUtils.assertExpectedValuesWithJsonPath(response, expectedValuesMap);
	}
}
