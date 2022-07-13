package ajbc.doodle.calendar.daos;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.entities.Notification;


@Transactional(rollbackFor = { DaoException.class }, readOnly = true)
public interface NotificationDao {

	// CRUD
	public default List<Notification> getAllNotifications() throws DaoException {
		throw new DaoException("Method not implemented");
	}
	
	@Transactional(readOnly = false)
	public default void addNotification(Notification notification) throws DaoException {
		throw new DaoException("Method not implemented");
	}
	
	@Transactional(readOnly = false)
	public default void updateNotification(Notification notification) throws DaoException {
		throw new DaoException("Method not implemented");
	}
	
	@Transactional(readOnly = false)
	public default void deleteNotification(Integer notificationId) throws DaoException {
		throw new DaoException("Method not implemented");
	}


	// Queries
	public default Notification getNotificationById(Integer id) throws DaoException {
		throw new DaoException("Method not implemented");
	}
	
	public default List<Notification> getUserNotifications(Integer userId) throws DaoException {
		throw new DaoException("Method not implemented");
	}
	
	public default List<Notification> getNotificationsByEventId(Integer eventId) throws DaoException {
		throw new DaoException("Method not implemented");
	}



}