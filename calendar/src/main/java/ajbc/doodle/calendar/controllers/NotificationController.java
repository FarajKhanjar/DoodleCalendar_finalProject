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

/**
 * Notification Controller that implements the Notification-API
 * @author Faraj
 *
 */
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
	
	/**
	 * This method get all notifications from the Sql dataBase
	 * @return list of notifications from the DB. or,
	 * @throws DaoException if there is a error to get 'notifications' list  or not found.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Notification>> getAllNotifications() throws DaoException {
		List<Notification> allNotifications = notificationService.getAllNotifications();
		if (allNotifications == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		return ResponseEntity.ok(allNotifications);
	}
	
	/**
	 * This method add a new event to the DataBase.
	 * @param notification: The body of the new event.
	 * @param userId: a key that shows for whom the user belong.
	 * @param eventId: a key that shows for whom the event belong.
	 * @return a new notification, or get a error message if add notification is fail.
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addNotification(@RequestBody Notification notification, @RequestParam Integer userId ,@RequestParam Integer eventId) {
		try {

			notificationService.addNotificationOfUserEvent(userId, eventId, notification);
			notificationManager.addNotification(notification);
			return ResponseEntity.status(HttpStatus.CREATED).build();
			
		} catch (DaoException e) {
			ErrorMessage errorMsg = new ErrorMessage();
			errorMsg.setData(e.getMessage());
			errorMsg.setMessage("Failed to add notification to the current event DB");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMsg);
		}
	}
	
	/**
	 * This method updates the a notification using the id.
	 * @param notification: The updated body of the notification.
	 * @param id: key to update and save the updated notification.
	 * @return a updated new notification or a error message if update notification is fail.
	 */
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
	
	/**
	 * This method updates a list of notification.
	 * @param newNotifications: The updated body of the notifications that we wont to update.
	 * @return a updated new notifications list or a error message if update notification is fail.
	 */
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

	/**
	 * This method get the notification that have this id number
	 * @param id: the key to search and get the notification.
	 * @return the notification that have this id, or
	 *        error message if searching for notification is fail to get.
	 */
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
	
	/**
	 * This method get all notifications of an event by event id
	 * @param id: the key to get the event from DB
	 * @return the notifications that belong to this event id.
	 */
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
	
	/**
	 * This method get all notifications of an user by user id
	 * @param id: the key to get the user from DB
	 * @return the notifications that belong to this user id.
	 */
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
	
	/**
	 * This method do a soft delete of the a notification using his id.
	 * @param notificationId: the key to get the notification from DB
	 * @return updated notification that have a value = 1 in failed = inActive. or,
	 * @throws DaoException if deleting softly of notification is fail.
	 */
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
	
	/**
	 * This method do a soft delete of a list of notifications.
	 * @param notificationList: the new body of all notifications
	 * @return updated notifications that have a value = 1 in failed = inActive. or,
	 * @throws DaoException if deleting softly of notifications list is fail.
	 */
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
	
	/**
	 * This method do a hard delete of the a notification using his id.
	 * @param notificationId: the key to get the notification from DB
	 * @return a message that the delete done or not, and delete notification totally from DB. or,
	 * @throws DaoException if deleting hardly of notification is fail.
	 */
	@RequestMapping(method = RequestMethod.DELETE, path="/hardDelete/{notificationId}")
	public ResponseEntity<?> deleteNotificationHardly(@PathVariable Integer notificationId) {
		
		try {
			notificationService.deleteNotificationHardly(notificationId);
			notificationManager.deleteNotification(notificationId);
			return ResponseEntity.status(HttpStatus.OK).body("Notification deleted!");
			
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("Failed to do hard delete to this notification");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMessage);
		}
	}
	
	/**
	 * This method do a hard delete of a list of notifications.
	 * @param notificationList: the new body of all notifications
	 * @return a message that the delete done or not, and delete notifications list totally from DB. or,
	 * @throws DaoException if deleting hardly of notifications list is fail.
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteNotificationsListHardly(@RequestBody List<Integer> notificationList) {
		
		try {
			notificationService.deleteNotificationsListHardly(notificationList);
			notificationList.forEach(oneNotification -> 
			              notificationManager.deleteNotification(oneNotification));
			return ResponseEntity.status(HttpStatus.OK).body("All notification deleted!");
			
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("Failed to do hard delete list to this notification");
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
	
	/**
	 * Start the NotificationManager -> run(), using dataManager.
	 * @throws DaoException
	 * @throws InterruptedException
	 */
	@Scheduled(initialDelay = 2000 ,fixedDelay = 6000)
	public void run() throws DaoException, InterruptedException {
		notificationManager.setDataManager(dataManager);
		List<Notification> notifications = notificationDao.getAllNotifications();
		notificationManager.initNotificationToQueue(notifications);
		notificationManager.run();
	}

}