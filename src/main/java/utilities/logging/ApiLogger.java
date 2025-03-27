package utilities.logging;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class ApiLogger implements Filter {
	private static final Logger logger = LogManager.getLogger(ApiLogger.class);

	@Override
	public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec,
			FilterContext ctx) {

		/*
		 * When {} is given Log4j defers the string formatting until it's actually
		 * needed, Saving CPU. The string concatenation ("Value: " + value) happens
		 * before the logger.info() method is called. Even if the log level is set
		 * higher (e.g., WARN or ERROR), the concatenation still occurs, wasting CPU and
		 * memory.
		 */

		logger.info("====== API REQUEST ======");
		logger.info("Request Method: {}", requestSpec.getMethod());
		logger.info("Request URL: {}", requestSpec.getURI());

		logger.info("Request Headers: ");
		requestSpec.getHeaders().asList()
				.forEach(header -> logger.info("	{}: {}", header.getName(), header.getValue()));

		logger.info("Query Params: ");
		requestSpec.getQueryParams().forEach((key, value) -> logger.info("	{}: {}", key, value));

		logger.info("Path Params: ");
		requestSpec.getPathParams().forEach((key, value) -> logger.info("	{}: {}", key, value));

		logger.info("Form Params:");
		requestSpec.getFormParams().forEach((key, value) -> logger.info("   {}: {}", key, value));

		logger.info("Cookies:");
		requestSpec.getCookies().asList()
				.forEach(cookie -> logger.info("	{}: {}", cookie.getName(), cookie.getValue()));

		logger.info("Authentication Scheme: {}", requestSpec.getAuthenticationScheme());
		logger.info("Config: {}", requestSpec.getConfig());
		logger.info("Defined Filters: {}", requestSpec.getDefinedFilters());
		logger.info("Proxy Specification: {}", requestSpec.getProxySpecification());
		logger.info("Port: {}", requestSpec.getPort());
		logger.info("HTTP Client: {}", requestSpec.getHttpClient());

		if (requestSpec.getBody() != null) {
			Object body = requestSpec.getBody();

			if (body instanceof String) {
				logger.info("Request Body:\n{}", body);
			} else if (body instanceof File) {
				try {
					String fileContent = new String(Files.readAllBytes(Paths.get(((File) body).getPath())));
					logger.info("Request Body:\n{}", fileContent);
				} catch (IOException e) {
					logger.error("Failed to read request body from file", e);
				}
			} else {
				logger.info("Request Body:\n{}", body.toString());
			}

		}

		Response response = ctx.next(requestSpec, responseSpec);

		logger.info("====== API RESPONSE ======");
		logger.info("Response Status: {}", response.getStatusCode());
		logger.info("Response Line: {}", response.getStatusLine());
		logger.info("Response Time: {} ms", response.getTime());

		logger.info("Response Headers:");
		response.getHeaders().asList().forEach(header -> logger.info("	{}: {}", header.getName(), header.getValue()));

		logger.info("Response Cookies:");
		response.getCookies().forEach((key, value) -> logger.info("   {}: {}", key, value));

		logger.info("Response Body:\n{}", response.getBody().asPrettyString());

		return response;
	}

}
