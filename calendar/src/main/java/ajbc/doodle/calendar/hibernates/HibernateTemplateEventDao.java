package ajbc.doodle.calendar.hibernates;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.EventDao;
import ajbc.doodle.calendar.entities.Address;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.enums.Category;

@Component(value = "HNT_event")
@SuppressWarnings("unchecked")
public class HibernateTemplateEventDao implements EventDao {

	@Autowired
	private HibernateTemplate template;

	// CRUD
	@Override
	public List<Event> getAllEvents() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Event.class);
		    //Set the result transformer to use.
		DetachedCriteria resultTransformer = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			//Each row of results is a distinct instance of the root entity.
		List<Event> resultList = (List<Event>) template.findByCriteria(resultTransformer);
		return resultList;
	}
	
	@Override
	public void addEvent(Event event) throws DaoException {
		template.persist(event);
	}
	
	@Override
	public void updateEvent(Event event) throws DaoException {
		template.merge(event);
	}
	
	@Override
	public void deleteEvent(Integer eventId) throws DaoException {
		Event event = getEventById(eventId);
		template.delete(event);
	}


	// Queries
	@Override
	public Event getEventById(Integer eventId) throws DaoException {
		Event event = template.get(Event.class, eventId);
		if(event == null)
			throw new DaoException("There is no such event in 'events' DB with id: "+eventId);
		return event;
	}
	
	@Override
	public List<Event> getEventsByCategoryId(Integer categoryId) throws DaoException  {
		List<Event> allEvents = getAllEvents();
		List<Event> eventsInCategory = new ArrayList<Event>();
		for(Event e : allEvents)
			if(e.getCategory() == Category.values()[categoryId-1])
				eventsInCategory.add(template.get(Event.class, e.getEventId()));

		if (eventsInCategory.isEmpty())
			throw new DaoException("There is no such events in category id: "+categoryId);
		return eventsInCategory;
	}
	
	@Override
	public List<Event> getEventsByCategoryName(String categoryName) throws DaoException  {
		List<Event> allEvents = getAllEvents();
		List<Event> eventsInCategory = new ArrayList<Event>();
		for(Event e : allEvents)
			if(e.getCategory() == Category.valueOf(categoryName))
				eventsInCategory.add(template.get(Event.class, e.getEventId()));

		if (eventsInCategory.isEmpty())
			throw new DaoException("There is no such events in "+categoryName+" category");
		return eventsInCategory;
	}

}