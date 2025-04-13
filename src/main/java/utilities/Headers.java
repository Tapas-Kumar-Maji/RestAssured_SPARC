package utilities;

import java.util.Map;

import headers.CorporateToSparcHeaders;

/**
 * Modify the default values of the headers using this class.
 */

public class Headers {

	/**
	 * For dummy AddPlaceApi testing.
	 * 
	 * @param contentType
	 * @return
	 */
	public Map<String, Object> addPlaceApi(String contentType) {

		Map<String, Object> headers_map = CorporateToSparcHeaders.getHeaders();
		headers_map.clear();
		headers_map.put(CorporateToSparcHeaders.Content_Type.getKey(), contentType);
		return headers_map;
	}

	/**
	 * For dummy UpdatePlaceApi testing.
	 * 
	 * @param contentType
	 * @return
	 */
	public Map<String, Object> updatePlaceApi(String contentType) {

		Map<String, Object> headers_map = CorporateToSparcHeaders.getHeaders();
		headers_map.clear();
		headers_map.put(CorporateToSparcHeaders.Content_Type.getKey(), contentType);
		return headers_map;
	}

	/**
	 * CreateSparcAuth API header.
	 * 
	 * @param client_ID
	 * @param client_Secret
	 * @param contentType
	 * @return
	 */
	public Map<String, Object> createSparcAuth(String client_ID, String client_Secret, String contentType) {

		Map<String, Object> headers_map = CorporateToSparcHeaders.getHeaders();
		if (client_ID != null) {
			headers_map.put(CorporateToSparcHeaders.Client_ID.getKey(), client_ID);
		}
		if (client_Secret != null) {
			headers_map.put(CorporateToSparcHeaders.Client_Secret.getKey(), client_Secret);
		}
		if (contentType != null) {
			headers_map.put(CorporateToSparcHeaders.Content_Type.getKey(), contentType);
	}

		return headers_map;
	}

}
