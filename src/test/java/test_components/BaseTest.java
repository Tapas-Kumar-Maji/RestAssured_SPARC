package test_components;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;

public class BaseTest {
	public String uri = null;
	public String version = null;
	public String username = null;
	private static final Logger logger = LogManager.getLogger(BaseTest.class);

	@BeforeSuite(alwaysRun = true)
	public void setProperties() {
		String env = System.getProperty("env", "").trim();
		Properties properties = this.readConfigDotPropertiesFile();

		if (env == null || env.isBlank() || env.isEmpty()) {
			env = "qa";
		}

		if (env.toLowerCase() == "qa") {
			this.uri = properties.getProperty("uri.qa");
		} else if (env.toLowerCase() == "sandbox") {
			this.uri = properties.getProperty("uri.sandbox");
		} else if (env.toLowerCase() == "production") {
			this.uri = properties.getProperty("uri.production");
		}

		// for dummy api testing hardcoding the uri property
		this.uri = properties.getProperty("uri.dummy");

		this.version = properties.getProperty("version");
		this.username = properties.getProperty("username");
	}

	/**
	 * Logs stack trace into .log file.
	 * 
	 * @param result
	 */
	@AfterMethod(alwaysRun = true)
	public void logStackTrace(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			logger.error("Error : \n", result.getThrowable());
		}
	}

	/**
	 * Reading config.properties file
	 * 
	 * @return java.util.Properties object.
	 */
	private Properties readConfigDotPropertiesFile() {
		Properties properties = new Properties();
		try {
			FileInputStream fis = new FileInputStream(
					System.getProperty("user.dir") + "/src/test/resources/config.properties");
			properties.load(fis);
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return properties;
	}
}
