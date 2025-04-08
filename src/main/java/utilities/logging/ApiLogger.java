package utilities.logging;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.ProxySpecification;

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

		logger.info("Multi Part Params:");
		requestSpec.getMultiPartParams().stream().forEach(m -> logger.info("   Name: {}, FileName: {}, MIME Type: {}",
				m.getControlName(), m.getFileName(), m.getMimeType()));

//		logger.info("Authentication Scheme: {}", requestSpec.getAuthenticationScheme());
		if (requestSpec.getAuthenticationScheme() != null) {
			logger.info("Authentication Scheme: {}", requestSpec.getAuthenticationScheme().getClass().getSimpleName());
		} else {
			logger.info("Authentication Scheme: {}", "[No Authentication]");
		}

//		logger.info("Config: {}", requestSpec.getConfig());
		RestAssuredConfig config = requestSpec.getConfig();
		logger.info("Request Configuration:");
		logger.info("  Follow Redirects: {}", config.getRedirectConfig().followsRedirects());

		Object socketTimeout = config.getHttpClientConfig().params().get("http.socket.timeout");
		int timeout = socketTimeout instanceof Number ? ((Number) socketTimeout).intValue() : 0;
		logger.info("  Socket Timeout: {} ms", timeout);

		Object connectionTimeout = config.getHttpClientConfig().params().get("http.connection.timeout");
		timeout = connectionTimeout instanceof Number ? ((Number) connectionTimeout).intValue() : 0;
		logger.info("  Connection Timeout: {} ms", timeout);

//		logger.info("Defined Filters: {}", requestSpec.getDefinedFilters());
		List<Filter> filters = requestSpec.getDefinedFilters();
		filters.forEach(filter -> logger.info("Applied Filter: {}", filter.getClass().getCanonicalName()));
		logger.info("Number of defined filters: {}", filters.size());

//		logger.info("Proxy Specification: {}", requestSpec.getProxySpecification());
		ProxySpecification proxySpec = requestSpec.getProxySpecification();

		if (proxySpec != null) {
			logger.info("Proxy Host: {}", proxySpec.getHost());
			logger.info("Proxy Port: {}", proxySpec.getPort());
			logger.info("Proxy Scheme: {}", proxySpec.getScheme());
		} else {
			logger.info("No Proxy is configured for this request.");
		}

//		logger.info("Port: {}", requestSpec.getPort());
		int port = requestSpec.getPort();
		if (port != -1) {
			logger.info("Request is set to use port: {}", port);
		} else {
			logger.info("No specific port is configured.");
		}

//		logger.info("HTTP Client: {}", requestSpec.getHttpClient());

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

		String responseBody = Optional.ofNullable(response.getBody()).map(b -> b.asPrettyString()).orElse("[No Body]");
		logger.info("Response Body:\n{}", responseBody);

		return response;
	}

}
