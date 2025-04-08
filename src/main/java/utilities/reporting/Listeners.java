package utilities.reporting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import utilities.slack.SlackUtils;

public class Listeners implements ITestListener {

	public static ExtentReports extentReports = null;
	public static ThreadLocal<ExtentTest> threadLocal = null;

	// later remove final and make public if required for all the below properties.
	private final List<String> allTests = new ArrayList<>();
	private int passedCount = 0;
	private int failedCount = 0;
	private int skippedCount = 0; 

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

		// Slack: Test Execution Summary
		String summary = "*Test Execution Summary:*\n" 
                + "*Passed:* " + passedCount + "\n"
                + "*Failed:* " + failedCount + "\n"
                + "*Skipped:* " + skippedCount;
		SlackUtils.sendMessage(summary);
			
		// Slack: All Test Results
		if (!allTests.isEmpty()) {
			String allResultsMessage = "*All Test Results:*\n" + String.join("\n", allTests);
			SlackUtils.sendMessage(allResultsMessage);
		}
	}

	@Override
	public void onTestStart(ITestResult result) {
		ExtentTest extentTest = extentReports.createTest("Test Name : " + result.getMethod().getMethodName());
		threadLocal.set(extentTest);
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		passedCount++;
		allTests.add(" *" + result.getMethod().getMethodName() + "* - PASSED");
	}
	
	@Override
	public void onTestSkipped(ITestResult result) {
		skippedCount++;
		allTests.add(" *" + result.getMethod().getMethodName() + "* - SKIPPED");

		ExtentReportManager.logSkipDetails("Test Skipped: " + result.getThrowable().getMessage());
	}

	@Override
	public void onTestFailure(ITestResult result) {
		failedCount++;
		allTests.add(" *" + result.getMethod().getMethodName() + "* - FAILED");

        StringBuilder errorDetails = new StringBuilder();
		errorDetails.append("*Test Failed:* `").append(result.getMethod().getMethodName()).append("`\n")
				.append("> *Error Message:* ```")
				.append(result.getThrowable().getMessage()).append("```\n");

        if (result.getThrowable().getCause() != null) {
            errorDetails.append("> *Error Cause:* ```").append(result.getThrowable().getCause().toString()).append("```\n");
        }

        errorDetails.append("> *Error Class:* `").append(result.getThrowable().getClass().getCanonicalName()).append("`\n");

		// Formatting and truncating Stack Trace for Slack
		String stackTraceForSlack = Arrays.stream(result.getThrowable().getStackTrace())
				.map(StackTraceElement::toString).collect(Collectors.joining("\n"));
		if (stackTraceForSlack.length() > 3000) {
			stackTraceForSlack = stackTraceForSlack.substring(0, 3000) + "\n...(truncated)";
		}

		errorDetails.append("> *Stack Trace:*\n```").append(stackTraceForSlack).append("```");

        // Send formatted error details to Slack
        SlackUtils.sendMessage(errorDetails.toString());

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
