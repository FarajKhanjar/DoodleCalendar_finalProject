package ajbc.doodle.calendar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.EventDao;
import ajbc.doodle.calendar.entities.Event;

@Component
public class EventService {
	
	@Autowired
	private EventDao eventDao;

	// CRUD
	public List<Event> getAllEvents() throws DaoException {
		return eventDao.getAllEvents();
	}
	
	public void addEvent(Event event, Integer userId) throws DaoException {
		eventDao.addEvent(event);
	}
	

	// Queries
	public Event getEventById(Integer eventId) throws DaoException {
		return eventDao.getEventById(eventId);
	}
	
}