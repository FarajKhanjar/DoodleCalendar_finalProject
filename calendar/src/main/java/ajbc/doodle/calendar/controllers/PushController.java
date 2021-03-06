/*package ajbc.doodle.calendar.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ajbc.doodle.calendar.Application;
import ajbc.doodle.calendar.ServerKeys;
import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.NotificationDao;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.webpush.PushMessage;
import ajbc.doodle.calendar.entities.webpush.Subscription;
import ajbc.doodle.calendar.entities.webpush.SubscriptionEndpoint;
import ajbc.doodle.calendar.services.CryptoService;
*/

//@RestController
//public class PushController {
//
//	
//	private final ServerKeys serverKeys;
//	private final CryptoService cryptoService;
//	private final Map<String, Subscription> subscriptions = new ConcurrentHashMap<>();
//	private final Map<String, Subscription> subscriptionsAngular = new ConcurrentHashMap<>();
//	private String lastNumbersAPIFact = "";
//	private final HttpClient httpClient;
//	private final Algorithm jwtAlgorithm;
//	private final ObjectMapper objectMapper;
//	private int counter;
//
//	@Autowired
//	private NotificationDao notificationDao; 
//	
//	@Autowired
//	private UserDao userDao;
//	
//	public PushController(ServerKeys serverKeys, CryptoService cryptoService, ObjectMapper objectMapper) {
//		this.serverKeys = serverKeys;
//		this.cryptoService = cryptoService;
//		this.httpClient = HttpClient.newHttpClient();
//		this.objectMapper = objectMapper;
//
//		this.jwtAlgorithm = Algorithm.ECDSA256(this.serverKeys.getPublicKey(), this.serverKeys.getPrivateKey());
//	}
//
//	@GetMapping(path = "/publicSigningKey", produces = "application/octet-stream")
//	public byte[] publicSigningKey() {
//		return this.serverKeys.getPublicKeyUncompressed();
//	}
//
//	@GetMapping(path = "/publicSigningKeyBase64")
//	public String publicSigningKeyBase64() {
//		return this.serverKeys.getPublicKeyBase64();
//	}
//
//	@PostMapping("/subscribe/{email}")
//	@ResponseStatus(HttpStatus.CREATED)
//	public void subscribe(@RequestBody Subscription subscription, @PathVariable(required = false) String email) {
//		// if user is registered allow subscription
//		this.subscriptions.put(subscription.getEndpoint(), subscription);
//		// for each user do 2 things
//		// 1. turn logged in flag to true
//		// 2 save 3 parameters in DB
//		System.out.println(subscription.getKeys().getP256dh());
//		System.out.println(subscription.getKeys().getAuth());
//		System.out.println(subscription.getEndpoint());
//		System.out.println("Subscription added with email " + email);
//	}
//
//	@PostMapping("/unsubscribe/{email}")
//	public void unsubscribe(@RequestBody SubscriptionEndpoint subscription,
//			@PathVariable(required = false) String email) {
//		this.subscriptions.remove(subscription.getEndpoint());
//		System.out.println("Subscription with email " + email + " got removed!");
//	}
//
//	@PostMapping("/isSubscribed")
//	public boolean isSubscribed(@RequestBody SubscriptionEndpoint subscription) {
//		return this.subscriptions.containsKey(subscription.getEndpoint());
//	}
//
////	@Scheduled(fixedDelay = 3_000)
////	public void testNotification() {
////		if (this.subscriptions.isEmpty()) {
////			return;
////		}
////		counter++;
////		try {
////			sendPushMessageToAllSubscribers(this.subscriptions, new PushMessage("message: " + counter, "Try to notify"));
////		} catch (JsonProcessingException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////	}
//	
//	public void testNotification() throws DaoException {
//		List<User> users = userDao.getAllUsers();
//		List<Notification> notifications = notificationDao.getAllNotifications();
//		Object message = new PushMessage(notifications.get(1).getTitle(), notifications.get(1).getMessage());
//
//		users.forEach(u -> {if(u.getUserOnline()==1) {
//			byte[] result;
//			try {
//				result = this.cryptoService.encrypt(this.objectMapper.writeValueAsString(message),
//						u.getSubscriptionInfo().getPublicKey(), u.getSubscriptionInfo().getAuthKey(), 0);
//				sendPushMessage(u.getSubscriptionInfo().getEndpoint(),result);
//				
//			} catch (InvalidKeyException  | NoSuchAlgorithmException | InvalidKeySpecException
//					| InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException
//					| BadPaddingException | JsonProcessingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		}
//			});
//	}
//
//	private void sendPushMessageToAllSubscribersWithoutPayload() {
//		Set<String> failedSubscriptions = new HashSet<>();
//		for (Subscription subscription : this.subscriptions.values()) {
//			boolean remove = sendPushMessage(subscription.getEndpoint(), null);
//			if (remove) {
//				failedSubscriptions.add(subscription.getEndpoint());
//			}
//		}
//		failedSubscriptions.forEach(this.subscriptions::remove);
//	}
//
//	private void sendPushMessageToAllSubscribers(Map<String, Subscription> subs, Object message)
//			throws JsonProcessingException {
//
//		Set<String> failedSubscriptions = new HashSet<>();
//
//		for (Subscription subscription : subs.values()) {
//			try {
//				byte[] result = this.cryptoService.encrypt(this.objectMapper.writeValueAsString(message),
//						subscription.getKeys().getP256dh(), subscription.getKeys().getAuth(), 0);
//				boolean remove = sendPushMessage(subscription, result);
//				if (remove) {
//					failedSubscriptions.add(subscription.getEndpoint());
//				}
//			} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException
//					| IllegalStateException | InvalidKeySpecException | NoSuchPaddingException
//					| IllegalBlockSizeException | BadPaddingException e) {
//				Application.logger.error("send encrypted message", e);
//			}
//		}
//
//		failedSubscriptions.forEach(subs::remove);
//	}
//
//	/**
//	 * @return true if the subscription is no longer valid and can be removed, false
//	 *         if everything is okay
//	 */
//	private boolean sendPushMessage(String endPoint, byte[] body) {
//		String origin = null;
//		try {
//			URL url = new URL(endPoint);
//			origin = url.getProtocol() + "://" + url.getHost();
//		} catch (MalformedURLException e) {
//			Application.logger.error("create origin", e);
//			return true;
//		}
//
//		Date today = new Date();
//		Date expires = new Date(today.getTime() + 12 * 60 * 60 * 1000);
//
//		String token = JWT.create().withAudience(origin).withExpiresAt(expires)
//				.withSubject("mailto:example@example.com").sign(this.jwtAlgorithm);
//
//		URI endpointURI = URI.create(endPoint);
//
//		Builder httpRequestBuilder = HttpRequest.newBuilder();
//		if (body != null) {
//			httpRequestBuilder.POST(BodyPublishers.ofByteArray(body)).header("Content-Type", "application/octet-stream")
//					.header("Content-Encoding", "aes128gcm");
//		} else {
//			httpRequestBuilder.POST(BodyPublishers.ofString("trytry"));
//			// httpRequestBuilder.header("Content-Length", "0");
//		}
//
//		HttpRequest request = httpRequestBuilder.uri(endpointURI).header("TTL", "180")
//				.header("Authorization", "vapid t=" + token + ", k=" + this.serverKeys.getPublicKeyBase64()).build();
//		try {
//			HttpResponse<Void> response = this.httpClient.send(request, BodyHandlers.discarding());
//
//			switch (response.statusCode()) {
//			case 201:
//				Application.logger.info("Push message successfully sent: {}", endPoint);
//				break;
//			case 404:
//			case 410:
//				Application.logger.warn("Subscription not found or gone: {}", endPoint);
//				// remove subscription from our collection of subscriptions
//				return true;
//			case 429:
//				Application.logger.error("Too many requests: {}", request);
//				break;
//			case 400:
//				Application.logger.error("Invalid request: {}", request);
//				break;
//			case 413:
//				Application.logger.error("Payload size too large: {}", request);
//				break;
//			default:
//				Application.logger.error("Unhandled status code: {} / {}", response.statusCode(), request);
//			}
//		} catch (IOException | InterruptedException e) {
//			Application.logger.error("send push message", e);
//		}
//		return false;	
//	}
//}