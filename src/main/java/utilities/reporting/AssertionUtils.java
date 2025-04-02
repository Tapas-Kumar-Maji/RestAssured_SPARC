package utilities.reporting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aventstack.extentreports.markuputils.MarkupHelper;

import io.restassured.response.Response;

public class AssertionUtils {

	public static void assertExpectedValuesWithJsonPath(Response response, Map<String, Object> expectedValuesMap) {

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

	}
}
