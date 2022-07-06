package ajbc.doodle.calendar.hibernates;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.NotificationDao;
import ajbc.doodle.calendar.entities.Notification;

@Component(value = "HNT_notification")
@SuppressWarnings("unchecked")
public class HibernateTemplateNotificationDao implements NotificationDao {

	@Autowired
	private HibernateTemplate template;

	// CRUD
	@Override
	public List<Notification> getAllNotifications() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Notification.class);
		return (List<Notification>) template.findByCriteria(criteria);
	}
	
	@Override
	public void addNotification(Notification notification) throws DaoException {
		template.persist(notification);
	}


	// Queries
	@Override
	public Notification getNotificationById(Integer notificationId) throws DaoException {
		Notification notification = template.get(Notification.class, notificationId);
		if (notification == null)
			throw new DaoException("There is no such notification in 'notifications' DB with id: ");
		return notification;
	}

}