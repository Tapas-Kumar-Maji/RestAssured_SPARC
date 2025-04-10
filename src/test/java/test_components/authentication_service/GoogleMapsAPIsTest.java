package test_components.authentication_service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import io.restassured.http.Method;
import io.restassured.response.Response;
import test_components.BaseTest;
import utilities.Headers;
import utilities.reporting.AssertionUtils;
import utilities.requests.Request;

public class GoogleMapsAPIsTest extends BaseTest {

    String place_id = null;
    String baseURI = "https://rahulshettyacademy.com";
    String updatedAddress = "Somyasundra Pally , Bangalore";

    Map<String, Object> pathParamsDummy = new HashMap<>(), multiPartParamsDummy = pathParamsDummy,
	    headersDummy = pathParamsDummy;
    Map<String, String> cookiesDummy = new HashMap<>();

    // POST Api Test.
    @Test
    public void addPlaceApi() {

	File addPlaceAPI_requestBody = new File(
		System.getProperty("user.dir") + "/src/test/resources/DummyApi/AddPlaceAPI_RequestBody.json");

	Headers headers = new Headers();
	Map<String, Object> headers_map = headers.addPlaceApi("application/json");

	Map<String, Object> queryParams = new HashMap<>();
	queryParams.put("key", "qaclick123");

	Request postRequest = new Request();
	Response response = postRequest.setRequestDetails(Method.POST, this.uri, "/maps/api/place/add/json",
		queryParams, pathParamsDummy, headers_map, cookiesDummy, multiPartParamsDummy, addPlaceAPI_requestBody);

	this.place_id = response.body().jsonPath().getString("place_id");

	// assertions.
	Map<String, Object> expectedValuesMap = new HashMap<>();
	expectedValuesMap.put("status", "OK");
	expectedValuesMap.put("scope", "APP");
//	expectedValuesMap.put("Server", "Apache/2.4.52 (Ubuntu)");
	AssertionUtils.assertExpectedValuesWithJsonPath(response, expectedValuesMap);

	response.then().assertThat().header("Server", "Apache/2.4.52 (Ubuntu)");
    }

    // PUT Api Test. (updating address)
    @Test
    public void updatePlaceApi() throws IOException, InterruptedException {

	Thread.sleep(5000L); // interval b/w requests.
	String endpoint = "/maps/api/place/update/json";
	String filePath = "src/test/resources/DummyApi/UpdatePlaceAPI_RequestBody.json";
	String requestBody = new String(Files.readAllBytes(Paths.get(filePath)));

	requestBody = requestBody.replaceFirst("#placeId", this.place_id).replaceFirst("#add", this.updatedAddress);

	Map<String, Object> queryParams = new HashMap<>();
	queryParams.put("place_id", this.place_id);
	queryParams.put("key", "qaclick123");

	Headers header = new Headers();
	Map<String, Object> headers = header.updatePlaceApi("application/json");

	Request updateRequest = new Request();
	updateRequest.setRequestDetails(Method.PUT, this.baseURI, endpoint, queryParams, pathParamsDummy, headers,
		cookiesDummy, multiPartParamsDummy, requestBody);
    }

    // GET Api Test.
    @Test(dependsOnMethods = { "updatePlaceApi" })
    public void getPlaceApiTest() {

	String endpoint = "/maps/api/place/get/json";

	Map<String, Object> queryParams = new HashMap<>();
	queryParams.put("place_id", this.place_id);
	queryParams.put("key", "qaclick123");

	Request getRequest = new Request();
	Response response = getRequest.setRequestDetails(Method.GET, this.baseURI, endpoint, queryParams,
		pathParamsDummy, headersDummy, cookiesDummy, multiPartParamsDummy, "");

	// assertions.
	Map<String, Object> expectedValuesMap = new HashMap<>();
	expectedValuesMap.put("address", this.updatedAddress);
	AssertionUtils.assertExpectedValuesWithJsonPath(response, expectedValuesMap);
    }

    // DELETE Api Test.
    @Test(dependsOnMethods = { "getPlaceApiTest" })
    public void deletePlaceApiTest() {

	String endpoint = "/maps/api/place/delete/json";

	Map<String, Object> queryParams = new HashMap<>();
	queryParams.put("key", "qaclick123");

	String body = "{\n" + "    \"place_id\":\"" + this.place_id + "\"\n" + "}";

	Request getRequest = new Request();
	Response response = getRequest.setRequestDetails(Method.DELETE, this.baseURI, endpoint, queryParams,
		pathParamsDummy, headersDummy, cookiesDummy, multiPartParamsDummy, body);

	// assertions.
	Map<String, Object> expectedValuesMap = new HashMap<>();
	expectedValuesMap.put("status", "OK");
	AssertionUtils.assertExpectedValuesWithJsonPath(response, expectedValuesMap);

	response.then().assertThat().body("status", Matchers.equalTo("OK"));
    }

}
