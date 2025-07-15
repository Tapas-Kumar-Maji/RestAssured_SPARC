package utilities.mail;

public class EmailFormatter {
	// Delete this comment
	/**
	 * Formats the Test Summary Details at the top of the email.
	 * 
	 * @param totalTestRun
	 * @param passedCount
	 * @param failedCount
	 * @param skippedCount
	 * @param retryCount
	 * @return
	 */
	public String formatEmail(int totalTestRun, int passedCount, int failedCount, int skippedCount, int retryCount) {
		return "<html>\n"
				+ "<head>\n"
				+ "<style>\n"
				+ "    body { font-family: Arial, sans-serif; }\n"
				+ "    table { border-collapse: collapse; width: 60%; margin: 20px 0; }\n"
				+ "    th, td { border: 1px solid #dddddd; padding: 8px; text-align: center; }\n"
				+ "    th { background-color: #4CAF50; color: white; }\n"
				+ "    .passed  { background-color: #28a745; color: #ffffff; }\n"
				+ "    .failed  { background-color: #dc3545; color: #ffffff; }\n"
				+ "    .skipped { background-color: #ffc107; color: #212529; }\n"
				+ "    .retried { background-color: #f2f2f2; color: #212529; }\n"
				+ "    .footer { margin-top: 20px; font-size: 12px; color: #888; }\n"
				+ "</style>\n"
				+ "</head>\n"
				+ "<body>\n"
				+ "\n"
				+ "<h2> Test Execution Summary</h2>\n"
				+ "\n"
				+ "<table>\n"
				+ "    <tr>\n"
				+ "        <th>Total Tests</th>\n"
				+ "        <th>Passed</th>\n"
				+ "        <th>Failed</th>\n"
				+ "        <th>Skipped</th>\n"
				+ "        <th>Retried</th>\n"
				+ "    </tr>\n"
				+ "    <tr>\n"
				+ "        <td>"+ totalTestRun +"</td>\n"
				+ "        <td class=\"passed\">" + passedCount + "</td>\n"
				+ "        <td class=\"failed\">" + failedCount + "</td>\n"
				+ "        <td class=\"skipped\">" + skippedCount + "</td>\n"
				+ "        <td class=\"retried\">" + retryCount + "</td>\n"
				+ "    </tr>\n"
				+ "</table>\n"
				+ "\n"
				+ "</body>\n"
				+ "</html>\n"
				+ "";
	}

	/**
	 * Formats into HTML table format, the map of test names and their status.
	 * 
	 * @param allResultsMessage
	 * @return
	 */
	public String convertToHtmlTableRows(String allResultsMessage) {
		StringBuilder htmlRows = new StringBuilder();

		// Split input by line
		String[] lines = allResultsMessage.split("\\r?\\n");

		for (String line : lines) {
			// Skip header line if present
			if (line.toLowerCase().contains("all test results")) {
				continue;
			}

			// Remove all asterisks and words wrapped in colons
			String cleaned = line.replaceAll("\\*", "") // remove *
					.replaceAll(":[^:\\s]+:", "") // remove words like :x:
					.trim();

			if (cleaned.isEmpty()) {
				continue;
			}

			// Split test name and status using " - " delimiter
			String[] parts = cleaned.split(" - ", 2);
			if (parts.length == 2) {
				String testName = parts[0].trim();
				String status = parts[1].trim();

				htmlRows.append("<tr><td>").append(testName).append("</td><td>").append(status).append("</td></tr>\n");
			}

		}
		return htmlRows.toString();
	}

}
