package headers;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains default Corporate_To_Sparc API headers.
 */

public enum CorporateToSparcHeaders {

	Client_ID("Client ID", "default_value"), Client_Secret("Client Secret", "default_value"),
	Content_Type("Content-Type", "application/json");

	private final String key;
	private final Object value;

	CorporateToSparcHeaders(String key, Object value) {
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

	public Object getValue() {
		return this.value;
	}

	/**
	 * Get a map containing headers of Corporate_To_Sparc API.
	 * 
	 * @return
	 */
	public static Map<String, Object> getHeaders() {
		Map<String, Object> headers_map = new HashMap<String, Object>();
		for (CorporateToSparcHeaders header : CorporateToSparcHeaders.values()) {

			if (headers_map.containsKey(header.getKey())) {
				throw new IllegalArgumentException("Duplicate headers found : " + header.getKey());
			}
			headers_map.put(header.getKey(), header.getValue());

		}
		return headers_map;
	}
}

// Delete this comment