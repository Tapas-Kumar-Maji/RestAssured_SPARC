package utilities;

import java.io.File;
import java.util.List;

import org.hamcrest.Matchers;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class PracticeForInterview {
	// Delete this comment
	static String placeId = null;

	public static void main(String[] args) {
		RestAssured.baseURI = "https://amazon.com";

		// Using POJO for Seralization (Rest Assured has internal libraries for serialization)
		POJO_Example pojo = new POJO_Example();
		pojo.setMessage("Hello World");
		pojo.setGreet("Hi");

		Car car = new Car();
		car.setCarName("Toyota");
		car.setPurchaseDate(23072024);

		pojo.setArr(new Object[] { car });

		add_place_api(pojo);
		System.out.println("Place Id : " + placeId);
		
		// update
		update_place_api();
		
		// Deseralization using POJO
		Response_POJO responsePojo = RestAssured
		.given()
		.log().all(true)
		.baseUri("https://rahulshettyacademy.com")
		.queryParam("key", "qaclick123")
		.header("Content-Type", "application/json")
		.body(new File(System.getProperty("user.dir") + "/src/main/resources/AddPlace.json"))
		
		.when()
		.post("/maps/api/place/add/json")
		.as(Response_POJO.class);

		System.out.println(responsePojo.getExpertise());
		Courses courses = responsePojo.getCourses();
		List<Child> apis = courses.getApi();
		
		
		// Request spec builder
		RequestSpecification req =  new RequestSpecBuilder()
		.setContentType(ContentType.JSON)
		.addQueryParam("key", "quack quack")
		.setBaseUri("https://google.com")
		.build();
		
		// Response spec builder
		ResponseSpecification res = new ResponseSpecBuilder()
		.expectStatusCode(200)
		.expectContentType(ContentType.JSON)
		.build();
		

		RestAssured.given()
		.spec(req)
		.body("{json}")
		.post("/api/endpoint")
		.then()
		.spec(res)
		.extract()
		.jsonPath();
		

	}
	
	private static void update_place_api() {
		
		RestAssured
		.given()
//		.log().all()
		.auth()
		.none()
//	    .auth().oauth2(token)  // works the same for Bearer tokens
		.baseUri("https://rahulshettyacademy.com")
		.queryParams("place_id", placeId)
		.queryParams("key", "qaclick123")
		.body("{\n"
				+ "\"place_id\":\"" + placeId + "\",\n"
				+ "\"address\":\"70 winter soilder, UK\",\n"
				+ "\"key\":\"qaclick123\"\n"
				+ "}\n"
				+ "")
//		.multiPart(new File(""))
		
		.when()
		.put("/maps/api/place/update/json")
		
		.then()
		.assertThat()
		.statusCode(200)
		.body("msg", Matchers.equalTo("Address successfully updated"));
		
		
		
	}

	private static void add_place_api(POJO_Example body) {

		Response response= RestAssured
				.given()
				.log().all(true)
				.baseUri("https://rahulshettyacademy.com")
				.queryParam("key", "qaclick123")
				.header("Content-Type", "application/json")
//				.body(new File(System.getProperty("user.dir") + "/src/main/resources/AddPlace.json"))
				.body(body)
				
				.when()
				.post("/maps/api/place/add/json")
				
				.then()
//				.log().all(true)
				
				.assertThat()
				.statusCode(200)
				.body("status", Matchers.containsStringIgnoringCase("ok"))
				.header("Server", Matchers.containsString("Apache/2.4"))
				
				.extract()
				.response()
				;

				System.out.println(response.asPrettyString());
				JsonPath jp = response.jsonPath();
				placeId = jp.getString("place_id");
	}

}
