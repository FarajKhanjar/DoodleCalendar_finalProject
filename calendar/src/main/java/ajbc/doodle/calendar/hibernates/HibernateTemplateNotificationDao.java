package ajbc.doodle.calendar.hibernates;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.hibernate.Criteria;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.NotificationDao;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;

@Component(value = "HNT_notification")
@SuppressWarnings("unchecked")
public class HibernateTemplateNotificationDao implements NotificationDao {

	@Autowired
	private HibernateTemplate template;

	// CRUD
	@Override
	public List<Notification> getAllNotifications() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Notification.class);
		     //Set the result transformer to use.
		DetachedCriteria resultTransformer = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		     //Each row of results is a distinct instance of the root entity.
		List<Notification> resultList = (List<Notification>) template.findByCriteria(resultTransformer);
		return resultList;
	}
	
	@Override
	public void addNotification(Notification notification) throws DaoException {
		template.persist(notification);
	}
	
	@Override
	public void updateNotification(Notification notification) throws DaoException {
		template.merge(notification);
	}
	
	@Override
	public void deleteNotification(Integer notificationId) throws DaoException {
		Notification notification = getNotificationById(notificationId);
		template.delete(notification);
	}


	// Queries
	@Override
	public Notification getNotificationById(Integer notificationId) throws DaoException {
		Notification notification = template.get(Notification.class, notificationId);
		if (notification == null)
			throw new DaoException("There is no such notification in 'notifications' DB with id: ");
		return notification;
	}
	
	@Override
	public List<Notification> getUserNotifications(Integer userId) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Notification.class);
		criteria.add(Restrictions.eq("userId", userId));
		return (List<Notification>) template
				.findByCriteria(criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY));
	}


}