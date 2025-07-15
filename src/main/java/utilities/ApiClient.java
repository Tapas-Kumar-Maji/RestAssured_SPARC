package utilities;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;

public class ApiClient {

	public Response sendRequest(Method method, String baseURI, Map<String, Object> queryParam,
			Map<String, String> headers_map, File body) {

		RestAssured.baseURI = baseURI;

		switch (method) {
		case GET:
			return given().queryParams(queryParam).headers(headers_map).when().get();
		case POST:
			return given().queryParams(queryParam).headers(headers_map).body(body).when().post();
		case PUT:
			return given().queryParams(queryParam).headers(headers_map).body(body).when().put();
		case DELETE:
			return given().queryParams(queryParam).headers(headers_map).when().delete();
		case PATCH:
			return given().queryParams(queryParam).headers(headers_map).body(body).when().patch();
		case HEAD:
			return given().queryParams(queryParam).headers(headers_map).when().head();
		case OPTIONS:
			return given().queryParams(queryParam).headers(headers_map).when().options();
		case TRACE:
			given().queryParams(queryParam).headers(headers_map).when().request(Method.TRACE);
		default:
			throw new IllegalArgumentException("Invalid HTTP method : " + method.toString());
		}
	}
}

//Delete this comment