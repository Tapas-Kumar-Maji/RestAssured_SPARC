package utilities.reporting;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Do not pass a <Map> or <List> in expectedValue and actualValue.
 * <Map> and <List> are mutable and not thread-safe.
 * <String>, primitive datatypes can be used as they are not mutable and thread-safe.
 */

@Getter
@AllArgsConstructor
public class AssertionKeys {

	private String jsonPath;
	private Object expectedValue;
	private Object actualValue;
	private String result;

}
