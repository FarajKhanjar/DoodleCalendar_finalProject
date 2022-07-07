package ajbc.doodle.calendar.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
	
	public void addNotification(Notification notification) throws DaoException {
		notificationDao.addNotification(notification);
	}

	// Queries
	public Notification getNotificationById(Integer NotificationId) throws DaoException {
		return notificationDao.getNotificationById(NotificationId);
	}
	
	@Transactional
	public List<Notification> getEventNotifications(Integer eventId) throws DaoException {
		Event event = eventDao.getEventById(eventId);
		return event.getNotifications().stream().collect(Collectors.toList());
	}
	
	public void addNotificationOfUserEvent(int userId, int eventId, Notification notification) throws DaoException {
		if (checkIfUserBelongToEvent(eventId, userId)==false)
			throw new DaoException("The current user doesnt Belong to this Event");
		
		notification.setEventToNotify(eventDao.getEventById(eventId));
		notification.setUserToNotify(userDao.getUserById(userId));
		notificationDao.addNotification(notification);
	}
	
	private boolean checkIfUserBelongToEvent(int eventId, int userId) throws DaoException {
		Event event = eventDao.getEventById(eventId);
		return event.getEventGuests().stream().map(User::getUserId).anyMatch(id -> id == userId);
	}

	
}