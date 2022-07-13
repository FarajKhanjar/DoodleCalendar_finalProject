package ajbc.doodle.calendar.managers;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

import ajbc.doodle.calendar.Application;
import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.webpush.PushMessage;
import ajbc.doodle.calendar.services.NotificationService;

public class PushManager implements Runnable {

	private DataManager dataManager;
	private User user;
	private Notification notification;
	
	@Autowired
	NotificationService notificationService;

	public PushManager(DataManager dataManager, User user, Notification notification) {

		this.dataManager = dataManager;
		this.user = user;
		this.notification = notification;

	}

	@Override
	public void run() {

		String pushMsg = "Event: " + notification.getEventToNotify().getTitle() + " | At: " + notification.getEventToNotify().getAddress() 
			+	" | Title: " + notification.getTitle()
		+ " | Message: " + notification.getMessage();
		
		PushMessage pushMsgObj = new PushMessage(notification.getTitle(), pushMsg);
		
		byte[] result;
		try {
			result = dataManager.getCryptoService().encrypt(
					dataManager.getObjectMapper().writeValueAsString(pushMsgObj),
					user.getSubscriptionInfo().getPublicKey(), user.getSubscriptionInfo().getAuthKey(), 0);

			sendPushMessage(user.getSubscriptionInfo().getEndpoint(), result);

			System.out.println(
					"Event: " + notification.getEventToNotify().getTitle() + " Title: " + notification.getTitle()
							+ " Message: " + notification.getMessage() + " sent to user " + user.getEmail());

				Thread.sleep(3000);

		} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException
				| InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("No more notifications right now.");
	}

	/**
	 * @return true if the subscription is no longer valid and can be removed, false
	 *         if everything is okay
	 */
	private boolean sendPushMessage(String endPoint, byte[] body) {
		String origin = null;
		try {
			URL url = new URL(endPoint);
			origin = url.getProtocol() + "://" + url.getHost();

		} catch (MalformedURLException e) {
			Application.logger.error("create origin", e);
			return true;
		}

		Date today = new Date();
		Date expires = new Date(today.getTime() + 12 * 60 * 60 * 1000);

		String token = JWT.create().withAudience(origin).withExpiresAt(expires)
				.withSubject("mailto:example@example.com").sign(dataManager.getJwtAlgorithm());

		URI endpointURI = URI.create(endPoint);

		Builder httpRequestBuilder = HttpRequest.newBuilder();
		if (body != null) {
			httpRequestBuilder.POST(BodyPublishers.ofByteArray(body)).header("Content-Type", "application/octet-stream")
					.header("Content-Encoding", "aes128gcm");
		} else {
			httpRequestBuilder.POST(BodyPublishers.ofString("trytrytry"));
            // httpRequestBuilder.header("Content-Length", "0");
		}

		HttpRequest request = httpRequestBuilder.uri(endpointURI).header("TTL", "180")
				.header("Authorization", "vapid t=" + token + ", k=" + dataManager.getServerKeys().getPublicKeyBase64())
				.build();
		try {
			HttpResponse<Void> response = dataManager.getHttpClient().send(request, BodyHandlers.discarding());

			switch (response.statusCode()) {
			case 201:
				Application.logger.info("Push message successfully sent: {}", endPoint);
				break;
			case 404:
			case 410:
				Application.logger.warn("Subscription not found or gone: {}", endPoint);
				// remove subscription from our collection of subscriptions
				return true;
			case 429:
				Application.logger.error("Too many requests: {}", request);
				break;
			case 400:
				Application.logger.error("Invalid request: {}", request);
				break;
			case 413:
				Application.logger.error("Payload size too large: {}", request);
				break;
			default:
				Application.logger.error("Unhandled status code: {} / {}", response.statusCode(), request);
			}

		} catch (IOException | InterruptedException e) {
			Application.logger.error("send push message", e);
		}

		return false;
	}

}