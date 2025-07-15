package test_components.authentication_service;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import test_components.BaseTest;
import utilities.Headers;
import utilities.reporting.AssertionUtils;
import utilities.requests.Request;

public class DummyApiTest extends BaseTest {

	String place_id = null;

	@Test(enabled = true)
	public void addPlaceApiTest() {

		RestAssured.baseURI = this.uri;
//		RestAssured.filters(new ApiLogger());
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
		Request postRequest = new Request();
		postRequest.setRequestDetails(Method.POST, this.uri, null, null, headers_map_dummy, headers_map_dummy,
				new HashMap<>(), new HashMap<>(), addPlaceAPI_requestBody);
//		postRequest.setRequestDetails(this.uri, "/maps/api/place/add/json", new HashMap<>(), new HashMap<>(),
//				headers_map_dummy, addPlaceAPI_requestBody);
	}

	@Test(enabled = true, dependsOnMethods = { "addPlaceApiTest_withFramework" })
	public void addPlaceApiTest_withFramework2() {

		File addPlaceAPI_requestBody = new File(
				System.getProperty("user.dir") + "/src/test/resources/DummyApi/AddPlaceAPI_RequestBody.json");

		Headers headers = new Headers();
		Map<String, Object> headers_map_dummy = headers.addPlaceApi("application/json");
		Request postRequest = new Request();
		postRequest.setRequestDetails(Method.POST, this.uri, "/maps/api/place/add/json", new HashMap<>(),
				new HashMap<>(), new HashMap<>(), new HashMap<>(), headers_map_dummy, addPlaceAPI_requestBody);
	}

	@Test
	public void addPlaceApiTest_withAssertions() {

		File addPlaceAPI_requestBody = new File(
				System.getProperty("user.dir") + "/src/test/resources/DummyApi/AddPlaceAPI_RequestBody.json");

		Headers headers = new Headers();
		Map<String, Object> headers_map_dummy = headers.addPlaceApi("application/json");

		Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("key", "qaclick123");

		Request postRequest = new Request();
		Response response = postRequest.setRequestDetails(Method.POST, this.uri, "/maps/api/place/add/json",
				queryParams, new HashMap<>(), headers_map_dummy, new HashMap<>(), new HashMap<>(),
				addPlaceAPI_requestBody);

		place_id = response.body().jsonPath().getString("place_id");

		Map<String, Object> expectedValuesMap = new HashMap<>();
		expectedValuesMap.put("status", "OK");
		expectedValuesMap.put("scope", "APP");
		AssertionUtils.assertExpectedValuesWithJsonPath(response, expectedValuesMap, "addPlaceApiTest_withAssertions");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void reqResPostAPITest() {

		Map emptyMap = new HashMap();
		String requestBody = "{\n" + "    \"name\": \"morpheus\",\n" + "    \"job\": \"leader\"\n" + "}";
		Headers headers = new Headers();
		Map<String, Object> headers_map = headers.addPlaceApi("application/json");

		Request postRequest = new Request();
		postRequest.setRequestDetails(Method.POST, "https://reqres.in/", "/api/users", emptyMap, emptyMap, headers_map,
				emptyMap, emptyMap, requestBody);
	}

	// get request test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test(dependsOnMethods = { "addPlaceApiTest_withAssertions" })
	public void getPlaceApiTest() {

		String baseURI = "https://rahulshettyacademy.com";
		String endpoint = "/maps/api/place/get/json";

		Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("place_id", this.place_id);
		queryParams.put("key", "qaclick123");

		Map dummyMap = new HashMap();

		Request postRequest = new Request();
		postRequest.setRequestDetails(Method.GET, baseURI, endpoint, queryParams, dummyMap, dummyMap, dummyMap,
				dummyMap, "");
	}

	@DataProvider(name = "DataOfNames")
	public String[][] getData() {
		return new String[][] { { "Tapas", "firstname" }, { "Kumar", "middle" }, { "Maji", "last" } };
	}

}
