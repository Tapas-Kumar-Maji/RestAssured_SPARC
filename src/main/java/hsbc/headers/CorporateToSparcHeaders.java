package hsbc.headers;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains default Corporate_To_Sparc API headers.
 */

public enum CorporateToSparcHeaders {

	Client_ID("Client ID", "default_value"), Client_Secret("Client Secret", "default_value"),
	Content_Type("Content-Type", "default_value");

	private final String key;
	private final String value;

	CorporateToSparcHeaders(String key, String value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * Always use this function when updating a header.
	 * 
	 * @return The header key.
	 */
	public String getKey() {
		return this.key;
	}

	public String getValue() {
		return this.value;
	}

	/**
	 * Get a map containing headers of Corporate_To_Sparc API.
	 * 
	 * @return
	 */
	public static Map<String, String> getHeaders() {
		Map<String, String> headers_map = new HashMap<String, String>();
		for (CorporateToSparcHeaders header : CorporateToSparcHeaders.values()) {

			if (headers_map.containsKey(header.getKey())) {
				throw new IllegalArgumentException("Duplicate key found : " + header.getKey());
			}
			headers_map.put(header.getKey(), header.getValue());

		}
		return headers_map;
	}
}
