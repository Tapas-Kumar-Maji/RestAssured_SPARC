package utilities.mail;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

public class EmailSender {
	// Delete this comment
	private static final Pattern REPORT_PATTERN = Pattern
			.compile("TestReport\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2}\\.html");

	/**
	 * Sends the email
	 * 
	 * @param toEmails
	 * @param emailBody
	 * @throws EmailException
	 */
	public static void sendEmailWithReport(String[] toEmails, String emailBody, String fallbackText) {

		try {
			// Configure email
//			MultiPartEmail email = new MultiPartEmail();
			HtmlEmail email = new HtmlEmail();
			email.setHostName("email-smtp.us-east-1.amazonaws.com"); // SES SMTP endpoint
			email.setSmtpPort(587); // Use 587 (STARTTLS) or 465 (SSL)
			email.setAuthenticator(new DefaultAuthenticator("AKIAVCR22KN3M2QAE2WQ", // SMTP Username
					"BIhsAB9N+8OufjZAqV6K1fWl7AxrxqYb37RsHPnOklzd" // SMTP Password
			));
			email.setStartTLSEnabled(true); // Required for port 587
			email.setFrom("no-reply@sparc-api-testing.programmableasset.io", "SPARC Automated Tests"); // MAIL FROM
			email.setSubject("Test Execution Report - " + new Date());
			email.setTextMsg(fallbackText);
			email.setHtmlMsg(emailBody);

			// Add recipients
			for (String toEmail : toEmails) {
				email.addTo(toEmail);
			}

			// Find the latest report dynamically
			String attachmentPath = findLatestReport(System.getProperty("user.dir") + "/reports");

			// Attach report
			EmailAttachment attachment = new EmailAttachment();
			attachment.setPath(attachmentPath);
			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			attachment.setDescription("Test Execution Report");
			attachment.setName("ExtentReport.html");
			email.attach(attachment);

			// Send email
			email.send();

			System.out.println("\n\nEmail sent successfully with report: " + attachmentPath + "\n");
		} 
		catch (EmailException e) {
			System.err.println("Failed to attach report: ");
			e.printStackTrace();
		}
	}

	/**
	 * Method to get the path of latest Extent Report in the directory
	 * 
	 * @param reportsDir
	 * @return filename
	 */
	private static String findLatestReport(String reportsDir) {
		File dir = new File(reportsDir);
		if (!dir.exists() || !dir.isDirectory()) {
			throw new RuntimeException("Reports directory not found: " + reportsDir);
		}

		// Filter files matching the pattern and sort by last modified
		File[] reportFiles = dir.listFiles((dir1, name) -> REPORT_PATTERN.matcher(name).matches());

		if (reportFiles == null || reportFiles.length == 0) {
			throw new RuntimeException("No matching Extent Report files found.");
		}

		// Sort by last modified (newest first)
		Arrays.sort(reportFiles, Comparator.comparingLong(File::lastModified).reversed());

		return reportFiles[0].getAbsolutePath(); // Latest report path
	}

}