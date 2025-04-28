package utilities.reporting;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import io.restassured.http.Header;

public class ExtentReportManager {

	public static ExtentReports createExtentReport(String absoluteFilePath, String reportName, String documentTitle) {

		ExtentSparkReporter extentSparkReporter = new ExtentSparkReporter(absoluteFilePath);
		extentSparkReporter.config().setReportName(reportName);
		extentSparkReporter.config().setDocumentTitle(documentTitle);
		extentSparkReporter.config().setTheme(Theme.STANDARD);
		extentSparkReporter.config().setEncoding("utf-8");

		ExtentReports extentReports = new ExtentReports();
		extentReports.attachReporter(extentSparkReporter);
		return extentReports;
	}

	/**
	 * @return A unique name when called. (Just the name ,not the file path)
	 */
	public static String getReportNameWithTimestamp() {

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
		LocalDateTime localDateTime = LocalDateTime.now();
		String formattedTime = dateTimeFormatter.format(localDateTime);
		String reportName = "TestReport" + formattedTime + ".html";
		return reportName;
	}

	public static void logPassDetails(String log) {
		if (Listeners.threadLocal == null) {
			return;
		}
		Listeners.threadLocal.get().pass(MarkupHelper.createLabel(log, ExtentColor.GREEN));
	}

	public static void logFailureDetails(String log) {
		if (Listeners.threadLocal == null) {
			return;
		}
		Listeners.threadLocal.get().fail(MarkupHelper.createLabel(log, ExtentColor.RED));
	}

	public static void logFailure(String log) {
		if (Listeners.threadLocal == null) {
			return;
		}
		Listeners.threadLocal.get().fail(log);
	}

	public static void logInfoDetails(String log) {
		if (Listeners.threadLocal == null) {
			return;
		}
		Listeners.threadLocal.get().info(log);
	}

	public static void logSkipDetails(String log) {
		if (Listeners.threadLocal == null) {
			return;
		}
		Listeners.threadLocal.get().skip(log);
	}

	/**
	 * @param json as <String>
	 */
	public static void logJson(String json) {
		if (Listeners.threadLocal == null) {
			return;
		}
		Listeners.threadLocal.get().info(MarkupHelper.createCodeBlock(json, CodeLanguage.JSON));
	}

	/**
	 * The type of json can be JsonPath in RestAssured, String, Map<String, Object>
	 * ,List<Map<String, Object>>, POJO, List<POJO>
	 * 
	 * @param json
	 */
	public static void logJsonObject(Object json) {
		if (Listeners.threadLocal == null) {
			return;
		}
		Listeners.threadLocal.get().info(MarkupHelper.createJsonCodeBlock(json));
	}

	/**
	 * For Headers
	 * 
	 * @param headers
	 */
	public static void logHeaders(List<Header> headersList) {
		if (Listeners.threadLocal == null) {
			return;
		}
		String[][] arrayHeaders = headersList.stream()
				.map(header -> new String[] { header.getName(), header.getValue() }).toArray(String[][]::new);
		Listeners.threadLocal.get().info(MarkupHelper.createTable(arrayHeaders));
	}

	/**
	 * For logging assertions in Extent Report.
	 * 
	 * @param assertions
	 */
	public static void logAssertions(String[][] assertions) {
		if (Listeners.threadLocal == null || assertions == null) {
			return;
		}
		Listeners.threadLocal.get().info(MarkupHelper.createTable(assertions));
	}

	/**
	 * Creates an ordered list from : Map, List, Set
	 */
	public static void logOrderedList(Object object) {
		if (Listeners.threadLocal == null) {
			return;
		}
		Listeners.threadLocal.get().info(MarkupHelper.createOrderedList(object));
	}

	/**
	 * Use this method for query, path and form parameters.
	 */
	public static void logParamsAsTable(Map<String, String> params) {
		if (Listeners.threadLocal == null) {
			return;
		}

		String[][] paramsArray = params.entrySet().stream()
				.map(entry -> new String[] { entry.getKey(), entry.getValue() }).toArray(String[][]::new);
		Listeners.threadLocal.get().info(MarkupHelper.createTable(paramsArray));
	}

}
