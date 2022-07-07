package ajbc.doodle.calendar.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.EventDao;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.User;

@Component
public class EventService {
	
	@Autowired
	private EventDao eventDao;

	@Autowired
	private UserDao userDao;
	
	// CRUD
	public List<Event> getAllEvents() throws DaoException {
		return eventDao.getAllEvents();
	}
	
	public void addEvent(Event event, Integer id) throws DaoException {
		event.setEventOwner(userDao.getUserById(id));
		eventDao.addEvent(event);
	}
	
	public void updateEvent(Event event) throws DaoException {
		eventDao.updateEvent(event);
	}
	

	// Queries
	public Event getEventById(Integer eventId) throws DaoException {
		return eventDao.getEventById(eventId);
	}
	
	@Transactional
	public List<Event> getUserEvents(Integer userId) throws DaoException {
		User user = userDao.getUserById(userId);
		return user.getEvents().stream().collect(Collectors.toList());
	}
	
	public boolean checkIfUserIsTheOwner(int eventId, int userId) throws DaoException {
		Event event = eventDao.getEventById(eventId);
		return (event.getEventOwnerId() == userId);
	}
	
}