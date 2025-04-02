package utilities.reporting;

//import lombok.AllArgsConstructor;
//import lombok.Getter;

//@Getter
//@AllArgsConstructor
public class AssertionKeys {

	private String jsonPath;
	private Object expectedValue;
	private Object actualValue;
	private String result;

	public AssertionKeys(String jsonPath, Object expectedValue, Object actualValue, String result) {
		this.jsonPath = jsonPath;
		this.expectedValue = expectedValue;
		this.actualValue = actualValue;
		this.result = result;
	}

	public String getJsonPath() {
		return jsonPath;
	}

	public Object getExpectedValue() {
		return expectedValue;
	}

	public Object getActualValue() {
		return actualValue;
	}

	public String getResult() {
		return result;
	}

}
