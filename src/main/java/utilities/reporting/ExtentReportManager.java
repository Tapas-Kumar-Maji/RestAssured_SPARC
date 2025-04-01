package utilities.reporting;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

	public static void logExceptionDetails(String log) {
		Listeners.threadLocal.get().fail(log);
	}

	public static void logInfoDetails(String log) {
		if (Listeners.threadLocal == null) {
			return;
		}
		Listeners.threadLocal.get().info(MarkupHelper.createLabel(log, ExtentColor.GREY));
	}

	public static void logWarningDetails(String log) {
		if (Listeners.threadLocal == null) {
			return;
		}
		Listeners.threadLocal.get().warning(MarkupHelper.createLabel(log, ExtentColor.YELLOW));
	}

	public static void logJson(String json) {
		if (Listeners.threadLocal == null) {
			return;
		}
		Listeners.threadLocal.get().info(MarkupHelper.createCodeBlock(json, CodeLanguage.JSON));
	}

	public static void logHeaders(List<Header> headersList) {
		String[][] arrayHeaders = headersList.stream()
				.map(header -> new String[] { header.getName(), header.getValue() }).toArray(String[][]::new);
		Listeners.threadLocal.get().info(MarkupHelper.createTable(arrayHeaders));
	}

}
