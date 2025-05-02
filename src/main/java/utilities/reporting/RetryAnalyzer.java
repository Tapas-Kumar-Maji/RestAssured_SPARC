package utilities.reporting;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

	private int retryCount = 0;
	private static final int maxRetryCount = 1; // Change to 2 or 3 for more retries
	public static Map<String, Integer> retryMap = new ConcurrentHashMap<>(); // new HashMap<>(); for thread-saftey
	public static Map<String, Integer> totalRetryCount = new ConcurrentHashMap<>(); // new HashMap<>(); for thread-saftey

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
