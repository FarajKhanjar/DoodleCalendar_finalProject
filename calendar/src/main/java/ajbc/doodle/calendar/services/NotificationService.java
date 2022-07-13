package ajbc.doodle.calendar.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.EventDao;
import ajbc.doodle.calendar.daos.NotificationDao;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;

@Component
public class NotificationService {
	
	@Autowired
	private NotificationDao notificationDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private EventDao eventDao;

	// CRUD
	public List<Notification> getAllNotifications() throws DaoException {
		return notificationDao.getAllNotifications();
	}
	
	public void addNotificationOfUserEvent(int userId, int eventId, Notification newNotification) throws DaoException {
		if (checkIfUserBelongToEvent(eventId, userId)==false)
			throw new DaoException("The current user doesnt Belong to this Event");
		
		newNotification.setEventToNotify(eventDao.getEventById(eventId));
		newNotification.setUserToNotify(userDao.getUserById(userId));
		notificationDao.addNotification(newNotification);
	}
	
    public void updateNotification(Notification newNotification, Integer id) throws DaoException {
    	Notification currentNotification = notificationDao.getNotificationById(id);
		newNotification.setNotificationId(id);

		newNotification.setUserToNotify(currentNotification.getUserToNotify());
		newNotification.setEventToNotify(currentNotification.getEventToNotify());
		//newNotification.setIsSent(currentNotification.getIsSent());
		
		notificationDao.updateNotification(newNotification);
	}
    
    public void deleteNotificationSoftly(Integer notificationId) throws DaoException {
		Notification notification = getNotificationById(notificationId);
		notification.setInActive(1);
		notificationDao.updateNotification(notification);
	}

	// Queries
	public Notification getNotificationById(Integer NotificationId) throws DaoException {
		return notificationDao.getNotificationById(NotificationId);
	}
	
	@Transactional(rollbackFor = {DaoException.class})
	public List<Notification> getEventNotifications(Integer eventId) throws DaoException {
		Event event = eventDao.getEventById(eventId);
		return event.getNotifications().stream().collect(Collectors.toList());
	}
	
	@Transactional(rollbackFor = {DaoException.class})
	public List<Notification> getUserNotifications(Integer userId) throws DaoException {
		userDao.getUserById(userId);
		return notificationDao.getUserNotifications(userId);
	}
	
	
	private boolean checkIfUserBelongToEvent(int eventId, int userId) throws DaoException {
		Event event = eventDao.getEventById(eventId);
		return event.getEventGuests().stream()
				.map(User::getUserId).anyMatch(i -> i == userId);
	}
	
	public void inActiveNotification(Integer notificationId) throws DaoException {
		Notification notification = getNotificationById(notificationId);
		notification.setInActive(1);
		//TODO make isSent inactive too
		notificationDao.updateNotification(notification);		
	}
	
	@Transactional(readOnly = false, rollbackFor = DaoException.class)
	public void updateNotifications(List<Notification> notifications) throws DaoException {
		for (Notification notification : notifications) {
			updateNotification(notification, notification.getNotificationId());
		}
	}
	
	public List<Notification> idsListOfNotifications(List<Integer> idsList) throws DaoException {
		List<Notification> notifications = new ArrayList<>();
		for (Integer notificationId : idsList) {
			notifications.add(getNotificationById(notificationId));
		}
		return notifications;
	}
	
	@Transactional
	public void deleteNotificationsListSoftly(List<Integer> notificationList) throws DaoException {
		notificationList.forEach(oneNotification-> {
			try {
				deleteNotificationSoftly(oneNotification);
				
			} catch (DaoException e) {
				e.printStackTrace();
			} });
	}
	
	

}