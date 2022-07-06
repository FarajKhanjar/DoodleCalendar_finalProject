package ajbc.doodle.calendar.hibernates;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.EventDao;
import ajbc.doodle.calendar.entities.Event;

@Component(value = "HNT_event")
@SuppressWarnings("unchecked")
public class HibernateTemplateEventDao implements EventDao {

	@Autowired
	private HibernateTemplate template;

	// CRUD
	@Override
	public List<Event> getAllEvents() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Event.class);
		return (List<Event>) template.findByCriteria(criteria);
	}
	
	@Override
	public void addEvent(Event event) throws DaoException {
		template.persist(event);
	}


	// Queries
	@Override
	public Event getEventById(Integer eventId) throws DaoException {
		Event event = template.get(Event.class, eventId);
		if(event == null)
			throw new DaoException("There is no such event in 'events' DB with id: "+eventId);
		return event;
	}

}