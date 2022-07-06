package ajbc.doodle.calendar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.NotificationDao;
import ajbc.doodle.calendar.entities.Notification;

@Component
public class NotificationService {
	
	@Autowired
	private NotificationDao notificationDao;

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
	
}