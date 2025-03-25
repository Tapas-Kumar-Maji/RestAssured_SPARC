package hsbc.utilities;

import java.util.HashMap;
import java.util.Map;

import hsbc.headers.CorporateToSparcHeaders;

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
	public Map<String, String> addPlaceApi(String contentType) {
		Map<String, String> headers_map = CorporateToSparcHeaders.getHeaders();
		headers_map.clear();
		headers_map.put(CorporateToSparcHeaders.Content_Type.getKey(), contentType);
		return headers_map;
	}

	public Map<String, String> corporateToSparc(String contentType) {
		Map<String, String> headers_map = CorporateToSparcHeaders.getHeaders();
		headers_map.put(CorporateToSparcHeaders.Content_Type.getKey(), contentType);
		return headers_map;
	}

	public Map<String, String> submitDCApplication(String contentType) {
		Map<String, String> headers = new HashMap<String, String>();
//		headers.put("Content-Type", contentType);
		return headers;
	}
}
