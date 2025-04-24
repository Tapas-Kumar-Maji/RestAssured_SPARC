package utilities.requests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.Filter;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.ProxySpecification;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.SpecificationQuerier;
import utilities.logging.ApiLogger;
import utilities.reporting.ExtentReportManager;

public class Request {

//    private static RequestSpecification requestSpecification = null;

    /**
     * Set the request details. (Consumes the body as a String)
     * 
     * @param method
     * @param baseURI
     * @param endpoint
     * @param queryParam
     * @param pathParams
     * @param headers
     * @param cookies
     * @param multiPartParams
     * @param body
     * @return
     */
    public Response setRequestDetails(Method method, String baseURI, String endpoint, Map<String, Object> queryParam,
	    Map<String, Object> pathParams, Map<String, Object> headers, Map<String, String> cookies,
	    Map<String, Object> multiPartParams, String body) {

    RequestSpecification requestSpecification = getRequestSpecification(baseURI, queryParam, pathParams, headers, cookies,
		multiPartParams, body);
	Response response = null;

	switch (method) {
	case GET:
	    response = requestSpecification.get(endpoint);
	    break;
	case POST:
	    response = requestSpecification.post(endpoint);
	    break;
	case PUT:
	    response = requestSpecification.put(endpoint);
	    break;
	case DELETE:
	    response = requestSpecification.delete(endpoint);
	    break;
	default:
	    throw new IllegalArgumentException("Invalid HTTP method : " + method.toString());
	}

	printRequestLogInExtentReport(requestSpecification);
	printResponseLogInExtentReport(response);
	return response;
    }

    /**
     * Set the request details. (Consumes the body as a java.io.File)
     * 
     * @param method
     * @param baseURI
     * @param endpoint
     * @param queryParam
     * @param pathParams
     * @param headers
     * @param cookies
     * @param multiPartParams
     * @param body
     * @return
     */
    public Response setRequestDetails(Method method, String baseURI, String endpoint, Map<String, Object> queryParam,
	    Map<String, Object> pathParams, Map<String, Object> headers, Map<String, String> cookies,
	    Map<String, Object> multiPartParams, File body) {

    RequestSpecification requestSpecification = getRequestSpecification(baseURI, queryParam, pathParams, headers, cookies,
		multiPartParams, body);
	Response response = null;

	switch (method) {
	case GET:
	    response = requestSpecification.get(endpoint);
	    break;
	case POST:
	    response = requestSpecification.post(endpoint);
	    break;
	case PUT:
	    response = requestSpecification.put(endpoint);
	    break;
	case DELETE:
	    response = requestSpecification.delete(endpoint);
	    break;
	default:
	    throw new IllegalArgumentException("Invalid HTTP method : " + method.toString());
	}

	printRequestLogInExtentReport(requestSpecification);
	printResponseLogInExtentReport(response);
	return response;
    }

    /**
     * Primary method that is used by the public methods in this class.
     */
    private static RequestSpecification getRequestSpecification(String baseURI, Map<String, Object> queryParam,
	    Map<String, Object> pathParams, Map<String, Object> headers, Map<String, String> cookies,
	    Map<String, Object> multiPartParams, Object body) {

	RequestSpecification requestSpecification = RestAssured.given().baseUri(baseURI).filters(new ApiLogger())
		.queryParams(queryParam).pathParams(pathParams).headers(headers).cookies(cookies);

	if (multiPartParams != null) {
	    multiPartParams.forEach((key, value) -> {
		if (value instanceof File) {
		    requestSpecification.multiPart(key, (File) value);
		} else if (value instanceof String) {
		    requestSpecification.multiPart(key, (String) value);
		} else {
		    throw new IllegalArgumentException("Multi-part params must be of type File or String");
		}
	    });
	}

	if (body != null) {
	    if (body instanceof String) {
		requestSpecification.body((String) body);
	    } else if (body instanceof File) {
		requestSpecification.body((File) body);
	    } else {
		throw new IllegalArgumentException("Body must be of type String, File, or JSON Map.");
	    }
	}

	return requestSpecification;
    }

    /**
     * Logs the request details in the ExtentReport.
     * 
     * @param requestSpecification
     */
    private static void printRequestLogInExtentReport(RequestSpecification requestSpecification) {

	QueryableRequestSpecification queryableRequestSpecification = SpecificationQuerier.query(requestSpecification);

	ExtentReportManager.logInfoDetails("====== API REQUEST ======");
	ExtentReportManager.logInfoDetails("Request Method: " + queryableRequestSpecification.getMethod());
	ExtentReportManager.logInfoDetails("Full URI: " + queryableRequestSpecification.getURI());
	ExtentReportManager.logInfoDetails("Request Headers: ");
	ExtentReportManager.logHeaders(queryableRequestSpecification.getHeaders().asList());

	ExtentReportManager.logInfoDetails("Query Params: ");
	ExtentReportManager.logOrderedList(queryableRequestSpecification.getQueryParams());
	ExtentReportManager.logInfoDetails("Path Params: ");
	ExtentReportManager.logOrderedList(queryableRequestSpecification.getPathParams());
	ExtentReportManager.logInfoDetails("Form Params: ");
	ExtentReportManager.logOrderedList(queryableRequestSpecification.getFormParams());

	queryableRequestSpecification.getCookies().asList().forEach(cookie -> ExtentReportManager
		.logInfoDetails("	Cookie name :" + cookie.getName() + "Cookie value : " + cookie.getValue()));

	for (MultiPartSpecification multiPart : queryableRequestSpecification.getMultiPartParams()) {
	    ExtentReportManager.logInfoDetails("Name : " + multiPart.getControlName());
	    ExtentReportManager.logInfoDetails("FileName : " + multiPart.getFileName());
	    ExtentReportManager.logInfoDetails("MIME Type : " + multiPart.getMimeType());
	}

	if (queryableRequestSpecification.getAuthenticationScheme() != null) {
	    ExtentReportManager.logInfoDetails("Authentication Scheme: "
		    + queryableRequestSpecification.getAuthenticationScheme().getClass().getSimpleName());
	} else {
	    ExtentReportManager.logInfoDetails("Authentication Scheme: " + "[No Authentication]");
	}

	RestAssuredConfig config = queryableRequestSpecification.getConfig();
	ExtentReportManager.logInfoDetails("Request Configuration:");
	ExtentReportManager.logInfoDetails("	Follow Redirects: " + config.getRedirectConfig().followsRedirects());

	Object socketTimeout = config.getHttpClientConfig().params().get("http.socket.timeout");
	int timeout = socketTimeout instanceof Number ? ((Number) socketTimeout).intValue() : 0;
	ExtentReportManager.logInfoDetails("	Socket Timeout: " + timeout + " ms");

	Object connectionTimeout = config.getHttpClientConfig().params().get("http.connection.timeout");
	timeout = connectionTimeout instanceof Number ? ((Number) connectionTimeout).intValue() : 0;
	ExtentReportManager.logInfoDetails("	Connection Timeout: " + timeout + " ms");

	List<Filter> filters = queryableRequestSpecification.getDefinedFilters();
	filters.forEach(filter -> ExtentReportManager
		.logInfoDetails("Applied Filter: " + filter.getClass().getCanonicalName()));
	ExtentReportManager.logInfoDetails("Number of defined filters: " + filters.size());

	ProxySpecification proxySpec = queryableRequestSpecification.getProxySpecification();
	if (proxySpec != null) {
	    ExtentReportManager.logInfoDetails("Proxy Host: " + proxySpec.getHost());
	    ExtentReportManager.logInfoDetails("Proxy Port: " + proxySpec.getPort());
	    ExtentReportManager.logInfoDetails("Proxy Scheme: " + proxySpec.getScheme());
	} else {
	    ExtentReportManager.logInfoDetails("No Proxy is configured for this request.");
	}

	int port = queryableRequestSpecification.getPort();
	if (port != -1) {
	    ExtentReportManager.logInfoDetails("Request is set to use port: " + port);
	} else {
	    ExtentReportManager.logInfoDetails("No specific port is configured.");
	}

	// This if else statement prints the body
	if (queryableRequestSpecification.getBody() != null) {
	    Object body = queryableRequestSpecification.getBody();

	    if (body instanceof String) {
		ExtentReportManager.logInfoDetails("Request Body:\n");
		ExtentReportManager.logJson(queryableRequestSpecification.getBody());
	    } else if (body instanceof File) {
		try {
		    String fileContent = new String(Files.readAllBytes(Paths.get(((File) body).getPath())));
		    ExtentReportManager.logInfoDetails("Request Body:\n");
		    ExtentReportManager.logJson(fileContent);
		} catch (IOException e) {
		    ExtentReportManager.logFailureDetails("Failed to read request body from file \n" + e);
		}
	    } else {
		ExtentReportManager.logInfoDetails("Request Body:\n");
		ExtentReportManager.logJson(queryableRequestSpecification.getBody().toString());
	    }
	}
    }

    /**
     * Logs the response details in the ExtentReport.
     * 
     * @param response
     */
    private static void printResponseLogInExtentReport(Response response) {

	ExtentReportManager.logInfoDetails("====== API RESPONSE ======");
	ExtentReportManager.logInfoDetails("Response status code: " + response.getStatusCode());
	ExtentReportManager.logInfoDetails("Response status line: " + response.getStatusLine());
	ExtentReportManager.logInfoDetails("Response Time: " + response.getTime() + " ms");

	ExtentReportManager.logInfoDetails("Response Headers: ");
	ExtentReportManager.logHeaders(response.getHeaders().asList());

	ExtentReportManager.logInfoDetails("Response Cookies:");
	ExtentReportManager.logParamsAsTable(response.getCookies());

	ExtentReportManager.logInfoDetails("Response Body:\n");
	String responseBody = Optional.ofNullable(response.getBody()).map(b -> b.asPrettyString()).orElse("[No Body]");
	ExtentReportManager.logJson(responseBody);
    }
}
