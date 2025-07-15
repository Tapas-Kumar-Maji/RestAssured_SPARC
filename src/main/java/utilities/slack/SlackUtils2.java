package utilities.slack;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.files.FilesCompleteUploadExternalRequest;
import com.slack.api.methods.request.files.FilesGetUploadURLExternalRequest;
import com.slack.api.methods.request.files.FilesUploadRequest;
import com.slack.api.methods.request.files.FilesUploadRequest.FilesUploadRequestBuilder;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.files.FilesCompleteUploadExternalResponse;
import com.slack.api.methods.response.files.FilesGetUploadURLExternalResponse;
import com.slack.api.methods.response.files.FilesUploadResponse;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Using Slack API
 */
public class SlackUtils2 {
	// Delete this comment
	private static final String BOT_TOKEN = "xoxb-..."; // Store securely in config.propertry
	private static final String CHANNEL_ID = "C08LRMYRQGH";
	private static String threadTs;
	private static final Logger LOGGER = LogManager.getLogger(SlackUtils2.class);

	private static void startSuiteThread(String message) {
		try {
			ChatPostMessageResponse response = Slack.getInstance().methods(BOT_TOKEN) // add config from SlackUtils
					.chatPostMessage(req -> req.channel(CHANNEL_ID).text(message));
			threadTs = response.getTs();
		} catch (IOException | SlackApiException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send a message to Slack using SlackAPI on a single thread.
	 * 
	 * @param message
	 */
	public static void sendMessage(String message) {
		if (threadTs == null) {
			startSuiteThread("*Test Started*\n.\n.\n.\n.\n");
		}
		try {
			Slack.getInstance().methods(BOT_TOKEN) // add config from SlackUtils
					.chatPostMessage(req -> req.channel(CHANNEL_ID).text(message).threadTs(threadTs));
		} catch (IOException | SlackApiException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Uploads a file to the Slack channel or thread.
	 *
	 * @param filePath       Full path to the file
	 * @param title          Display title of the file
	 * @param initialComment Optional message with the upload
	 * @param uploadToThread Whether to attach to the thread
	 */
	 public static void uploadFileToSlack(String filePath, String title, String initialComment, boolean uploadToThread) {
	        File file = new File(filePath);

	        if (!file.exists()) {
				LOGGER.error("File not found: " + filePath);
	            return;
			}

	        try {
				FilesUploadRequestBuilder requestBuilder = FilesUploadRequest.builder()
	                    .token(BOT_TOKEN)
	                    .channels(Collections.singletonList(CHANNEL_ID))
	                    .file(file)
	                    .filename(file.getName())
	                    .title(title)
	                    .initialComment(initialComment);
				
	            if (uploadToThread && threadTs != null) {
	                requestBuilder.threadTs(threadTs);
	            }

	            FilesUploadResponse response = Slack.getInstance().methods().filesUpload(requestBuilder.build());

	            if (!response.isOk()) {
					LOGGER.error("Slack file upload failed: " + response.getError());
	            }

	        } catch (IOException | SlackApiException e) {
	            e.printStackTrace();
	        }
	    }

		/**
		 * Uploads a file to the Slack channel or thread.
		 * 
		 * @param filePath
		 * @param title
		 * @param initialComment
		 * @param uploadToThread
		 */
	 public static void uploadFileToSlack2(String filePath, String title, String initialComment, boolean uploadToThread) {
	        File file = new File(filePath);

			if (!file.exists()) {
				LOGGER.error("File not found: " + filePath);
				return;
			}

	        try {

				long maxAllowed = Integer.MAX_VALUE;
				if (file.length() > maxAllowed) {
					throw new IllegalArgumentException("File too large (max " + maxAllowed + " bytes)");
				}

	            // Step 1: Request upload URL
	            FilesGetUploadURLExternalResponse uploadUrlResponse = Slack.getInstance().methods(BOT_TOKEN)
	                    .filesGetUploadURLExternal(FilesGetUploadURLExternalRequest.builder()
	                            .filename(file.getName())
								.length((int) Math.min(file.length(), maxAllowed))
	                            .build());

				if (!uploadUrlResponse.isOk()) {
					LOGGER.error("Failed to get upload URL: " + uploadUrlResponse.getError());
					return;
				}
				
				// Step 2: Upload file as multipart/form-data
	            MultipartBody multipartBody = new MultipartBody.Builder()
	                    .setType(MultipartBody.FORM)
	                    .addFormDataPart(
	                            "file", 
	                            file.getName(),
								RequestBody.create(file, MediaType.parse("application/octet-stream")))
						.build();

				// Step 3: Upload the file to the provided URL
	            Response uploadResponse = Slack.getInstance()
	                    .getHttpClient()
						.postMultipart(uploadUrlResponse.getUploadUrl(), BOT_TOKEN, multipartBody);

				if (!uploadResponse.isSuccessful()) {
					LOGGER.error("File upload failed: " + uploadResponse.message());
					return;
				}
	              
	            // Step 4: Complete the upload
				FilesCompleteUploadExternalResponse completeResponse = Slack.getInstance()
					    .methods(BOT_TOKEN)
					    .filesCompleteUploadExternal(FilesCompleteUploadExternalRequest.builder()
					        .files(Collections.singletonList(
					            FilesCompleteUploadExternalRequest.FileDetails.builder()
					                .id(uploadUrlResponse.getFileId())
					                .title(title)
					                .build()
					        ))
					        .channelId(CHANNEL_ID)
					        .initialComment(initialComment)
					        .threadTs(uploadToThread ? threadTs : null)
					        .build());

				if (!completeResponse.isOk()) {
					LOGGER.error("Failed to complete file upload: " + completeResponse.getError());
				}

	        } catch (IOException | SlackApiException e) {
	            e.printStackTrace();
	        }
	    }
}
