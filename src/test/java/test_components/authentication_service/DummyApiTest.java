package test_components.authentication_service;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;

import org.testng.annotations.Test;

import hsbc.utilities.Headers;
import io.restassured.RestAssured;
import test_components.BaseTest;

public class DummyApiTest extends BaseTest {

	@Test
	public void addPlaceApiTest() {

		RestAssured.baseURI = this.uri;
		File addPlaceAPI_requestBody = new File(
				System.getProperty("user.dir") + "/src/test/resources/DummyApi/AddPlaceAPI_RequestBody.json");
		Headers headers = new Headers();

//		Is APIClient class necessary?
//		Doing without API client
		given().log().all().queryParam("key", "qaclick123").headers(headers.addPlaceApi("application/json"))
				.body(addPlaceAPI_requestBody).post("/maps/api/place/add/json").then().log().all().statusCode(200)
				.body("scope", equalTo("APP"));
	}
}
