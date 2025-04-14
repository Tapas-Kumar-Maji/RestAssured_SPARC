package utilities.reporting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import utilities.slack.SlackUtils;

public class Listeners implements ITestListener {

	private static final Logger logger = LogManager.getLogger(Listeners.class);

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

		logger.info("Test PASSED: {}", result.getMethod().getMethodName());
	}
	
	@Override
	public void onTestSkipped(ITestResult result) {
		skippedCount++;
		allTests.add(" *" + result.getMethod().getMethodName() + "* - SKIPPED");
		
		// Log to Extent Report
		Throwable throwable = result.getThrowable();
		if (throwable != null) {
			StringBuilder skipDetails = new StringBuilder();

			skipDetails.append("<b>Test Skipped:</b> ")
					.append(result.getMethod().getMethodName())
					.append("<br><b>Skip Reason:</b> ")
					.append(throwable.getMessage())
					.append("<br>");

			if (throwable.getCause() != null) {
				skipDetails.append("<b>Cause:</b> ")
						.append(throwable.getCause().toString())
						.append("<br>");
			}

			skipDetails.append("<b>Exception Class:</b> ")
					.append(throwable.getClass().getCanonicalName())
					.append("<br>");

			// Stack trace formatting
			String stackTraceFormatted = Arrays.stream(throwable.getStackTrace())
					.map(StackTraceElement::toString)
					.collect(Collectors.joining("<br>"));

			if (stackTraceFormatted.length() > 8000) {
				stackTraceFormatted = stackTraceFormatted.substring(0, 8000) + "<br>...(truncated)";
			}
			skipDetails.append("<details>\n" + "  <summary>Click to see stack trace</summary>\n")
					.append("<b>Stack Trace:</b><br><pre>").append(stackTraceFormatted).append("</pre>")
					.append("\n" + "</details>\n");

			ExtentReportManager.logSkipDetails(skipDetails.toString());
		}

		
		// Slack: Log throwable
		StringBuilder skipDetails = new StringBuilder();
		skipDetails.append("*Test Skipped:* `").append(result.getMethod().getMethodName()).append("`\n")
				.append("> *Skip Reason:* ```").append(result.getThrowable().getMessage()).append("```\n");

		if (result.getThrowable().getCause() != null) {
			skipDetails.append("> *Skip Cause:* ```").append(result.getThrowable().getCause().toString())
					.append("```\n");
		}

		skipDetails.append("> *Exception Class:* `").append(result.getThrowable().getClass().getCanonicalName())
				.append("`\n");

		// Stack trace (shortened)
		String stackTraceForSlack = Arrays.stream(result.getThrowable().getStackTrace())
				.map(StackTraceElement::toString).collect(Collectors.joining("\n"));
		if (stackTraceForSlack.length() > 3000) {
			stackTraceForSlack = stackTraceForSlack.substring(0, 3000) + "\n...(truncated)";
		}
		skipDetails.append("> *Stack Trace:*\n```").append(stackTraceForSlack).append("```");

		// Send to Slack
		SlackUtils.sendMessage(skipDetails.toString());

		// Log to .log file
		logToFile(result, "SKIPPED");
		logger.warn("Test SKIPPED: {}", result.getMethod().getMethodName());

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

		errorDetails.append("> *Error Class:* `").append(result.getThrowable().getClass().getCanonicalName())
				.append("`\n");

		// Formatting and truncating Stack Trace for Slack
		String stackTraceForSlack = Arrays.stream(result.getThrowable().getStackTrace())
				.map(StackTraceElement::toString).collect(Collectors.joining("\n"));
		if (stackTraceForSlack.length() > 3000) {
			stackTraceForSlack = stackTraceForSlack.substring(0, 3000) + "\n...(truncated)";
		}

		errorDetails.append("> *Stack Trace:*\n```").append(stackTraceForSlack).append("```");

        // Send formatted error details to Slack
        SlackUtils.sendMessage(errorDetails.toString());


		// Log to Extent Report
		Throwable throwable = result.getThrowable();
		StringBuilder failureDetails = new StringBuilder();

		failureDetails.append("<b>Test Failed:</b> ").append(result.getMethod().getMethodName())
				.append("<br><b>Error Message:</b> ").append(throwable.getMessage()).append("<br>");

		if (throwable.getCause() != null) {
			failureDetails.append("<b>Cause:</b> ").append(throwable.getCause().toString()).append("<br>");
		}

		failureDetails.append("<b>Exception Class:</b> ").append(throwable.getClass().getCanonicalName())
				.append("<br>");

		// Stack trace formatting
		String stackTraceFormatted = Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString)
				.collect(Collectors.joining("<br>"));

		if (stackTraceFormatted.length() > 8000) {
			stackTraceFormatted = stackTraceFormatted.substring(0, 8000) + "<br>...(truncated)";
		}

		failureDetails.append("<details>\n").append("<summary><b>Click to view stack trace</b></summary>\n")
				.append("<pre>").append(stackTraceFormatted).append("</pre>\n</details>");

		ExtentReportManager.logFailure(failureDetails.toString());

		// Log to .log file
		logToFile(result, "FAILED");
		logger.error("Test FAILED: {}", result.getMethod().getMethodName());

	}

	private void logToFile(ITestResult result, String status) {
		Throwable throwable = result.getThrowable();
		if (throwable == null) {
			return;
		}

		StringBuilder logBuilder = new StringBuilder();
		logBuilder.append("\n==================== ").append(status).append(" ====================\n");
		logBuilder.append("Test Name      : ").append(result.getMethod().getMethodName()).append("\n");
		logBuilder.append("Error Message  : ").append(throwable.getMessage()).append("\n");

		if (throwable.getCause() != null) {
			logBuilder.append("Cause          : ").append(throwable.getCause().toString()).append("\n");
		}

		logBuilder.append("Exception Class: ").append(throwable.getClass().getCanonicalName()).append("\n");
		logBuilder.append("Stack Trace    :\n");

		String stackTrace = Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString)
				.collect(Collectors.joining("\n"));

		if (stackTrace.length() > 10000) {
			stackTrace = stackTrace.substring(0, 10000) + "\n...(truncated)";
		}

		logBuilder.append(stackTrace).append("\n");

		logger.error(logBuilder.toString());
	}

}
