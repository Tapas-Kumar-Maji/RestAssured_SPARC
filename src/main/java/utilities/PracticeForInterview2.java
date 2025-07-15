package utilities;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.Matchers;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class PracticeForInterview2 {

	public static void main(String[] args) {

		// default baseURI for all requests to be made.
		RestAssured.baseURI = "https://rahulshettyacademy.com";
		
		Response getPlaceResponse =
		given()
		.baseUri("https://rahulshettyacademy.com")
		.queryParam("key", "qaclick123")
		.header("content-type", "application/json")
		.body("{\n"
				+ "    \"location\": {\n"
				+ "        \"lat\": -38.383494,\n"
				+ "        \"lng\": 33.427362\n"
				+ "    },\n"
				+ "    \"accuracy\": 50,\n"
				+ "    \"name\": \"Tapas Kumar\",\n"
				+ "    \"phone_number\": \"(+91) 983 893 3937\",\n"
				+ "    \"address\": \"29, side layout, cojen 09\",\n"
				+ "    \"types\": [\n"
				+ "        \"shoe park\",\n"
				+ "        \"shop\"\n"
				+ "    ],\n"
				+ "    \"website\": \"http://google.com\",\n"
				+ "    \"language\": \"French-IN\"\n"
				+ "}")
		.when()
		.post("/maps/api/place/add/json")
		
		.then()
		.log().all(true)
		.assertThat()
		.statusCode(200)
		.body("scope", equalTo("APP"))
						.header("Server", Matchers.containsString("Apache/2.4.52"))

						.extract().response();
		
		
	}
}
