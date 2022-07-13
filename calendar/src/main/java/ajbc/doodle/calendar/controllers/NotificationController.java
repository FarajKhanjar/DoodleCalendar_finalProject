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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.NotificationDao;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.managers.DataManager;
import ajbc.doodle.calendar.managers.NotificationManager;
import ajbc.doodle.calendar.services.NotificationService;


@RequestMapping("/notifications")
@RestController
public class NotificationController {

	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private NotificationDao notificationDao;

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
	public ResponseEntity<?> updateNotification(@RequestBody Notification notification, @PathVariable Integer id) {
		
		try {			
			notificationService.updateNotification(notification, id);
			notification = notificationService.getNotificationById(id);
			notificationManager.updateNotification(notification);
			System.out.println("send::" +notification.getIsSent());
			return ResponseEntity.status(HttpStatus.OK).body(notification);
		} 
		catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("failed to update notification in db");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMessage);
		}
	}
	
	@RequestMapping(method = RequestMethod.PUT, path = "/list")
	public ResponseEntity<?> updateNotificationsFromList(@RequestBody List<Notification> newNotifications) {
		try {
			notificationService.updateNotifications(newNotifications);
			newNotifications = notificationService.idsListOfNotifications(
					newNotifications.stream().map(
							notification -> notification.getNotificationId()).collect(Collectors.toList()));
			notificationManager.updateListOfNotifications(newNotifications);
			return ResponseEntity.status(HttpStatus.OK).body(newNotifications);

		}  catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("Failed to update list of notifications");
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

		}
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/byUserId/{id}")
	public ResponseEntity<?> getUserNotifications(@PathVariable Integer id)  {
		try {
			List<Notification> notifications = notificationService.getUserNotifications(id);
			return ResponseEntity.ok(notifications);
			
		} catch (DaoException e) {
			ErrorMessage errorMsg = new ErrorMessage();
			errorMsg.setData(e.getMessage());
			errorMsg.setMessage("Failed to get notifications of this user.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMsg);

		}
	}
	
	@RequestMapping(method = RequestMethod.PUT, path="/softDelete/{notificationId}")
	public ResponseEntity<?> deleteNotificationSoftly(@PathVariable Integer notificationId) {
		
		try {
			notificationService.deleteNotificationSoftly(notificationId);
			Notification notification = notificationService.getNotificationById(notificationId);
			notificationManager.deleteNotification(notificationId);
			return ResponseEntity.status(HttpStatus.OK).body(notification);
			
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("Failed to do soft delete to this notification");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMessage);
		}
	}
	
	@RequestMapping(method = RequestMethod.PUT, path="/softDeleteList")
	public ResponseEntity<?> deleteNotificationsListSoftly(@RequestBody List<Integer> notificationList) {
		
		try {
			notificationService.deleteNotificationsListSoftly(notificationList);
			notificationList.forEach(oneNotification -> 
			notificationManager.deleteNotification(oneNotification));
			return ResponseEntity.status(HttpStatus.OK).body("Soft Delete done");
			
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("Failed to do soft delete list to this notification");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMessage);
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
	
	@Scheduled(initialDelay = 2000 ,fixedDelay = 6000)
	public void run() throws DaoException, InterruptedException {
		notificationManager.setDataManager(dataManager);
		List<Notification> notifications = notificationDao.getAllNotifications();
		notificationManager.initNotificationToQueue(notifications);
		notificationManager.run();
	}

}