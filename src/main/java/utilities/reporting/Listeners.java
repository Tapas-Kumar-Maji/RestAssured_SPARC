package utilities.reporting;

import java.util.Arrays;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public class Listeners implements ITestListener {

	public static ExtentReports extentReports = null;
	public static ThreadLocal<ExtentTest> threadLocal = null;

	@Override
	public void onStart(ITestContext context) {
		String fileName = ExtentReportManager.getReportNameWithTimestamp();
		String absoluteFilePath = System.getProperty("user.dir") + "/reports/" + fileName;
		extentReports = ExtentReportManager.createExtentReport(absoluteFilePath, "API Testing Report",
				"Test Execution Report");
		threadLocal = new ThreadLocal<>();
	}

	@Override
	public void onFinish(ITestContext context) {
		if (extentReports != null) {
			extentReports.flush();
		}
	}

	@Override
	public void onTestStart(ITestResult result) {
		ExtentTest extentTest = extentReports.createTest("Test Name : " + result.getMethod().getMethodName());
		threadLocal.set(extentTest);
	}

	@Override
	public void onTestSuccess(ITestResult result) {

	}

	@Override
	public void onTestFailure(ITestResult result) {

		ExtentReportManager.logFailureDetails("Error Message : " + result.getThrowable().getMessage());
		if (result.getThrowable().getCause() != null) {
			ExtentReportManager.logFailureDetails("Error cause : " + result.getThrowable().getCause().toString());
		}
		ExtentReportManager
				.logFailureDetails("Error classname : " + result.getThrowable().getClass().getCanonicalName());
		String stackTrace = Arrays.toString(result.getThrowable().getStackTrace());
		stackTrace = stackTrace.replace(",", "<br>");
		String formattedStackTrace = "<details>\n" + "  <summary>Click to see stack trace</summary>\n" + stackTrace
				+ "\n" + "</details>\n";
		ExtentReportManager.logExceptionDetails(formattedStackTrace);
	}

}
