package utilities.slack;

import java.io.IOException;
import java.util.ArrayList;

import com.slack.api.Slack;
import com.slack.api.SlackConfig;
import com.slack.api.util.http.listener.HttpResponseListener;
import com.slack.api.webhook.Payload;

public class SlackUtils {

	// This webhook url is for sparc-api-testing channel.
	public static final String WEBHOOK_URL = "https://hooks.slack.com/services/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";

	/**
	 * Sends the message to Slack webhook url.
	 * 
	 * @param message
	 */
	public static void sendMessage(String message) {

		SlackConfig config = new SlackConfig(); // disables slack response logging to console
		config.setHttpClientResponseHandlers(new ArrayList<HttpResponseListener>());

		Slack slack = Slack.getInstance(config);
		Payload payload = Payload.builder().text(message).build();
		try {
			slack.send(WEBHOOK_URL, payload);
//			WebhookResponse response = slack.send(WEBHOOK_URL, payload);
//			System.out.println("Slack response : " + response.getBody());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
