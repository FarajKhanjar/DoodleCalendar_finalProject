package ajbc.doodle.calendar.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.EventDao;
import ajbc.doodle.calendar.daos.NotificationDao;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.Notification;

@Component
public class EventService {
	
	@Autowired
	private EventDao eventDao;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	NotificationDao notificationDao;
	
	@Autowired
	private UserService userService;
	
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
	
	public void deleteEventSoftly(Integer eventId) throws DaoException {
		Event event = getEventById(eventId);
		event.setInActive(1);		
		eventDao.updateEvent(event);		
	}
	
	public void deleteEventHardly(Integer eventId) throws DaoException {
		Event event = getEventById(eventId);
		
		Set<Notification> eventNotifications = event.getNotifications();
		eventNotifications.forEach(oneNotification-> {
			try {
				notificationDao.deleteNotification(oneNotification.getNotificationId());
				
			} catch (DaoException e) {
				e.printStackTrace();
			} });
		eventDao.deleteEvent(eventId);
	}
	
	// Queries
	public Event getEventById(Integer eventId) throws DaoException {
		return eventDao.getEventById(eventId);
	}
	
	@Transactional(rollbackFor = {DaoException.class})
	public List<Event> getUserEvents(Integer userId) throws DaoException {
		User user = userDao.getUserById(userId);
		List<Event> userEventsList = user.getEvents().stream().collect(Collectors.toList());
		List<Event> resultEventsList = new ArrayList<>();
		for (Event oneEvent : userEventsList) {
			Set<Notification> userEventNotifications = oneEvent.getNotifications().stream().filter(
					notificationOfUser -> notificationOfUser.getUserId() == userId)
					.collect(Collectors.toSet());
			oneEvent.setNotifications(userEventNotifications);
			resultEventsList.add(oneEvent);		
		}
		
		return resultEventsList;
	}
	
	public boolean checkIfUserIsTheOwner(int eventId, int userId) throws DaoException {
		Event event = eventDao.getEventById(eventId);
		boolean isUserOwner = (event.getEventOwnerId() == userId);
		return isUserOwner;
	}
	
	public List<Event> getEventsByCategoryId(Integer categoryId) throws DaoException {
		return eventDao.getEventsByCategoryId(categoryId);
	}
	
	public List<Event> getEventsByCategoryName(String categoryName) throws DaoException {
		return eventDao.getEventsByCategoryName(categoryName);
	}
	
	public void inActiveEvent(Integer eventId) throws DaoException {
		Event event = getEventById(eventId);
		event.setInActive(1);
		eventDao.updateEvent(event);
	}
	
	@Transactional
	public List<Event> getFutureEventsOfUser(Integer userId) throws DaoException {
		List<Event> allUserEvents = getUserEvents(userId);
		return allUserEvents.stream().filter(oneEvent-> oneEvent.getStartDateTime().isAfter(LocalDateTime.now())).toList();
	}
	
	@Transactional
	public List<Event> getUserEventsInDateRange(LocalDateTime startDate, LocalDateTime endDate, Integer userId) throws DaoException {
		List<Event> allUserEvents = getUserEvents(userId);
		return allUserEvents.stream().filter(oneEvent-> oneEvent.getStartDateTime().isAfter(startDate) &&
				                         oneEvent.getEndDateTime().isBefore(endDate)).toList();
	}
	
	@Transactional
	public List<Event> getAllEventsInDateRange(LocalDateTime startDate, LocalDateTime endDate) throws DaoException {
		List<Event> allEvents = eventDao.getAllEvents();
		return allEvents.stream().filter(oneEvent->oneEvent.getStartDateTime().isAfter(startDate) && 
				                        oneEvent.getEndDateTime().isBefore(endDate)).toList();
	}
	
	public List<Event> getNextEventsOfUserByHoursMinutes(Integer hours, Integer minutes, Integer userId) throws DaoException {
		LocalDateTime startDate = LocalDateTime.now();
		LocalDateTime endDate = startDate.plusHours(hours).plusMinutes(minutes);
		System.out.println("------[Search for events in range dateTime of:]------");
		System.out.println(startDate+" -> "+endDate);
		return getUserEventsInDateRange(startDate, endDate, userId);
	}
	
	public List<Event> getUserEventsByCategoryName(Integer userId,String categoryName) throws DaoException {
		List<Event> events = eventDao.getEventsByCategoryName(categoryName);
		List<Event> userEvents = new ArrayList<Event>();;
		for(Event e : events) {
			if(e.getEventOwner().getUserId()==userId)
				userEvents.add(getEventById(e.getEventId()));
		}
	
		return userEvents;
	}
	
	@Transactional
	public void deleteEventsListSoftly(List<Integer> eventsList) throws DaoException {
		eventsList.forEach(oneEvent -> {
			try {
				deleteEventSoftly(oneEvent);
				
			} catch (DaoException e) {
				e.printStackTrace();
			} });
	}
	
	@Transactional
	public void deleteEventsListHardly(List<Integer> eventsList) throws DaoException {
		eventsList.forEach(oneNotification -> { try {
				deleteEventHardly(oneNotification);
				
			} catch (DaoException e) {
				e.printStackTrace();
			} });
	}
	
}