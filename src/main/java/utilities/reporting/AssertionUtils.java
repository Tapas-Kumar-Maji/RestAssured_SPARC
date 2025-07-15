package utilities.reporting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aventstack.extentreports.markuputils.MarkupHelper;

import io.restassured.response.Response;
import utilities.slack.SlackUtils;

public class AssertionUtils {
	// Delete this comment
	/**
	 * Use this method only to assert jsonPath values. (Do not assert headers,
	 * cookies etc.) (Do not assert if a jsonPath is present using this method.)
	 * 
	 * @param response
	 * @param expectedValuesMap
	 * @param testName
	 */
	public static void assertExpectedValuesWithJsonPath(Response response, Map<String, Object> expectedValuesMap,
			String testName) {

		// Logging assertions to Extent Reports

		List<AssertionKeys> assertionList = new ArrayList<>();
		assertionList.add(new AssertionKeys("JSON PATH", "EXPECTED VALUE", "ACTUAL VALUE", "RESULT"));

		boolean allMatched = true;
		Set<String> jsonPaths = expectedValuesMap.keySet();

		for (String jsonPath : jsonPaths) {

			if (response.jsonPath().get(jsonPath) != null) {
				Object actualValue = response.jsonPath().get(jsonPath);
				if (actualValue.equals(expectedValuesMap.get(jsonPath))) {
					assertionList
							.add(new AssertionKeys(jsonPath, expectedValuesMap.get(jsonPath), actualValue, "MATCHED"));
		} else {
					allMatched = false;
					assertionList.add(
							new AssertionKeys(jsonPath, expectedValuesMap.get(jsonPath), actualValue, "NOT_MATCHED"));
		}
			} else {
				allMatched = false;
				assertionList.add(new AssertionKeys(jsonPath, expectedValuesMap.get(jsonPath), "", "NOT_MATCHED"));
			}
		}

		if (allMatched) {
			ExtentReportManager.logPassDetails("All Assertions have passed");
		} else {
			ExtentReportManager.logFailureDetails("All Assertions not passed");
		}

		String[][] arrayHeaders = assertionList.stream()
				.map(assertion -> new String[] { assertion.getJsonPath(), String.valueOf(assertion.getExpectedValue()),
						String.valueOf(assertion.getActualValue()), assertion.getResult() })
				.toArray(String[][]::new);
		Listeners.threadLocal.get().info(MarkupHelper.createTable(arrayHeaders));

		// Logging assertions to Slack

		StringBuilder slackTable = new StringBuilder();
		slackTable.append("*Assertion Results for Test:* `" + testName + "`\n");
		slackTable.append("```");
		slackTable.append(
				String.format("| %s | %s | %s | %s |\n", "JSON PATH", "EXPECTED VALUE", "ACTUAL VALUE", "RESULT"));
		slackTable.append("=".repeat(85)).append("\n");
		assertionList.remove(0);

		for (AssertionKeys assertion : assertionList) {

			String colorResult = "MATCHED".equals(assertion.getResult()) ? "MATCHED" : "NOT_MATCHED";
			slackTable.append(String.format("| %s | %s | %s | %s |\n", assertion.getJsonPath(),
					String.valueOf(assertion.getExpectedValue()), String.valueOf(assertion.getActualValue()),
					colorResult));

		}

		slackTable.append("```");
		SlackUtils.sendMessage(slackTable.toString());

		// Failing the test
		if (!allMatched) {
			throw new AssertionError("Assertion(s) failed. Check Extent Report or Slack for details.");
	}
	}
}
