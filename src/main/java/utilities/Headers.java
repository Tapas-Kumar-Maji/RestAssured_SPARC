package utilities;

import java.util.HashMap;
import java.util.Map;

import headers.CorporateToSparcHeaders;

/**
 * Modify the default values of the headers using this class.
 */

public class Headers {

	/**
	 * For Dummy API testing
	 * 
	 * @param contentType
	 * @return map of headers
	 */
	public Map<String, Object> addPlaceApi(String contentType) {
		Map<String, Object> headers_map = CorporateToSparcHeaders.getHeaders();
		headers_map.clear();
		headers_map.put(CorporateToSparcHeaders.Content_Type.getKey(), contentType);
		return headers_map;
	}

	public Map<String, Object> corporateToSparc(String contentType) {
		Map<String, Object> headers_map = CorporateToSparcHeaders.getHeaders();
		headers_map.put(CorporateToSparcHeaders.Content_Type.getKey(), contentType);
		return headers_map;
	}

	public Map<String, Object> submitDCApplication(String contentType) {
		Map<String, Object> headers = new HashMap<String, Object>();
//		headers.put("Content-Type", contentType);
		return headers;
	}
}
