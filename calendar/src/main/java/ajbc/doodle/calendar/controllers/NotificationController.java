package ajbc.doodle.calendar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.NotificationDao;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.webpush.PushMessage;
import ajbc.doodle.calendar.managers.DataManager;
import ajbc.doodle.calendar.managers.NotificationManager;
import ajbc.doodle.calendar.services.NotificationService;
import ajbc.doodle.calendar.Application;

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

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.core.JsonProcessingException;


@RequestMapping("/notifications")
@RestController
public class NotificationController {

	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private NotificationDao notificationDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private DataManager dataManager;

	@Autowired
	private NotificationManager notificationManager;
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Notification>> getAllNotifications() throws DaoException {
		List<Notification> allNotifications = notificationService.getAllNotifications();
		if (allNotifications == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		return ResponseEntity.ok(allNotifications);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addNotification(@RequestBody Notification notification, @RequestParam Integer userId ,@RequestParam Integer eventId) {
		try {

			notificationService.addNotificationOfUserEvent(userId, eventId, notification);
			return ResponseEntity.status(HttpStatus.CREATED).build();
			
		} catch (DaoException e) {
			ErrorMessage errorMsg = new ErrorMessage();
			errorMsg.setData(e.getMessage());
			errorMsg.setMessage("Failed to add notification to the current event DB");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMsg);
		}
	}
	
	@RequestMapping(method = RequestMethod.PUT, path = "/{id}")
	public ResponseEntity<?> updateNotification(@RequestBody Notification notification, @PathVariable Integer id, @RequestParam Integer userId) {
		
		try {
			//TODO fix updating notification
			notification.setNotificationId(id);
			notificationService.updateNotification(notification, userId);
			notification = notificationService.getNotificationById(notification.getNotificationId());
			return ResponseEntity.status(HttpStatus.OK).body(notification);
			
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("failed to update notification in db");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMessage);
		}
	}

	@RequestMapping(method = RequestMethod.GET, path = "/byId/{id}")
	public ResponseEntity<?> getNotificationById(@PathVariable Integer id) throws DaoException {
		try {
			Notification notification = notificationService.getNotificationById(id);
			return ResponseEntity.ok(notification);
			
		} catch (DaoException e) {
			ErrorMessage errorMsg = new ErrorMessage();
			errorMsg.setData(e.getMessage());
			errorMsg.setMessage("Failed to get notification By this Id");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMsg);
		}

	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/byEventId/{id}")
	public ResponseEntity<?> getEventNotifications(@PathVariable Integer id)  {
		try {
			List<Notification> notifications = notificationService.getEventNotifications(id);
			return ResponseEntity.ok(notifications);
			
		} catch (DaoException e) {
			ErrorMessage errorMsg = new ErrorMessage();
			errorMsg.setData(e.getMessage());
			errorMsg.setMessage("Failed to get notifications of this event.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMsg);
			//TODO fix null list return
		}
	}
	
	
	//// Push notifications Control
	
	@GetMapping(path = "/publicSigningKey", produces = "application/octet-stream")
	public byte[] publicSigningKey() {
		return dataManager.getServerKeys().getPublicKeyUncompressed();
	}

	@GetMapping(path = "/publicSigningKeyBase64")
	public String publicSigningKeyBase64() {
		return dataManager.getServerKeys().getPublicKeyBase64();
	}

	@PostConstruct
	public void update() throws DaoException {
		notificationManager.setManagerData(dataManager);
		List<Notification> notifications = notificationDao.getAllNotifications();
		notificationManager.addNotification(notifications);
	}
	
	@Scheduled(fixedDelay = 3_000)
	public void testNotification() throws DaoException {
		List<User> users = userDao.getAllUsers();
		List<Notification> notifications = notificationDao.getAllNotifications();
		Object message = new PushMessage(notifications.get(1).getTitle(), notifications.get(1).getMessage());

		users.forEach(oneUser -> {
			if (oneUser.getUserOnline()==1) {
				byte[] result;
				try {
					result = dataManager.getCryptoService().encrypt(dataManager.getObjectMapper().writeValueAsString(message),
							oneUser.getSubscriptionInfo().getPublicKey(),
							oneUser.getSubscriptionInfo().getAuthKey(), 0);
					sendPushMessage(oneUser.getSubscriptionInfo().getEndpoint(), result);
					
				} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException
						| InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException
						| BadPaddingException | JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

//	/**
//	 * @return true if the subscription is no longer valid and can be removed, false
//	 *         if everything is okay
//	 */
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
				.header("Authorization", "vapid t=" + token + ", k=" + dataManager.getServerKeys().getPublicKeyBase64()).build();
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