package utilities.reporting;

import java.util.HashMap;
import java.util.Map;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

	private int retryCount = 0;
	private static final int maxRetryCount = 2; // Change to 2 or 3 for more retries
	public static Map<String, Integer> retryMap = new HashMap<>();
	public static Map<String, Integer> totalRetryCount = new HashMap<>();

	@Override
	public boolean retry(ITestResult result) {

		if (retryCount < maxRetryCount) {

			retryCount++;
			result.setWasRetried(true);
			retryMap.put(result.getName(), retryCount);
			totalRetryCount.put("totalRetry", totalRetryCount.getOrDefault("totalRetry", 0) + 1);

			return true;
		}
		return false;
	}

}
