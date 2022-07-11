package ajbc.doodle.calendar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.Address;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.services.EventService;


@RequestMapping("/events")
@RestController
public class EventController {

	@Autowired
	private EventService eventService;
	
	@Autowired
	private UserDao userDao;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Event>> getAllEvents() throws DaoException {
		List<Event> events = eventService.getAllEvents();
		return ResponseEntity.ok(events);
	}

	@RequestMapping(method = RequestMethod.POST, path = "/{userId}")
	public ResponseEntity<?> addEvent(@RequestBody Event event, @PathVariable Integer userId) {
		try {
			eventService.addEvent(event,userId);
			event = eventService.getEventById(event.getEventId());
			return ResponseEntity.status(HttpStatus.CREATED).body(event);
			
		} catch (DaoException e) {
			ErrorMessage errorMsg = new ErrorMessage();
			errorMsg.setData(e.getMessage());
			errorMsg.setMessage("Failed to add event to DB");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMsg);
		}
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<?> updateEvent(@RequestBody Event event, @RequestParam Integer userId ,@RequestParam Integer eventId) {
		try {
			if(eventService.checkIfUserIsTheOwner(eventId,userId)==false)
				throw new DaoException("Just the event owner can edit his event.");
			event.setEventId(eventId);
			eventService.updateEvent(event);
			event = eventService.getEventById(eventId);
			return ResponseEntity.status(HttpStatus.OK).body(event);
			
		} catch (DaoException e) {
			ErrorMessage errorMsg = new ErrorMessage();
			errorMsg.setData(e.getMessage());
			errorMsg.setMessage("Failed to update event in DB");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMsg);
		}
	}

	@RequestMapping(method = RequestMethod.GET, path = "/byId/{id}")
	public ResponseEntity<?> getEventById(@PathVariable Integer id) {
		try {
			Event event = eventService.getEventById(id);
			return ResponseEntity.ok(event);
			
		} catch (DaoException e) {
			ErrorMessage errorMsg = new ErrorMessage();
			errorMsg.setData(e.getMessage());
			errorMsg.setMessage("Failed to get event By this Id");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMsg);
		}

	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/byUserId/{userId}")
	public ResponseEntity<?> getUserEvents(@PathVariable Integer userId) {
		try {
			List<Event> allUserEvents = eventService.getUserEvents(userId);
			return ResponseEntity.ok(allUserEvents);
			
		} catch (DaoException e) {
			ErrorMessage errorMsg = new ErrorMessage();
			errorMsg.setData(e.getMessage());
			errorMsg.setMessage("Failed to get user events");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMsg);
			//TODO fix null list return
		}

	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/byCategoryId/{categoryId}")
	public ResponseEntity<?> getEventsByCategoryId(@PathVariable Integer categoryId) {
		
		List<Event> eventsOfCategory;
		try {
			eventsOfCategory = eventService.getEventsByCategoryId(categoryId);
			return ResponseEntity.ok(eventsOfCategory);
			
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("Failed to get events By this category id");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
			//TODO fix null list return
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/byCategoryName/{categoryName}")
	public ResponseEntity<?> getEventsByCategoryName(@PathVariable String categoryName) {
		
		List<Event> eventsOfCategory;
		try {
			eventsOfCategory = eventService.getEventsByCategoryName(categoryName.toUpperCase());
			return ResponseEntity.ok(eventsOfCategory);
			
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("Failed to get events By this category");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMessage);
			//TODO fix null list return
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, path="/futureEvents/{userId}")
	public ResponseEntity<?> getFutureEventsOfUser(@PathVariable Integer userId) {
		
		List<Event> list;
		try {
			list = eventService.getFutureEventsOfUser(userId);
			if (list == null)
				return ResponseEntity.notFound().build();
			return ResponseEntity.ok(list);
			
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("Failed to get future events of this user with id "+userId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
		}		
	}
	
	@RequestMapping(method = RequestMethod.GET, path="/userEventsRange/{userId}")
	public ResponseEntity<?> getUserEventsInDateRange(@PathVariable Integer userId) { 

//, @RequestParam String startDate ,@RequestParam String endDate) {		
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//		LocalDateTime start = LocalDateTime.parse(startDate, formatter);
//		LocalDateTime end = LocalDateTime.parse(endDate, formatter);
//		System.out.println(start+"->"+end);
		
		List<Event> list;
		try {
			list = eventService.getUserEventsInDateRange(LocalDateTime.now(), LocalDateTime.now().plusDays(30), userId);
			if (list == null)
				return ResponseEntity.notFound().build();
			return ResponseEntity.ok(list);
			
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("Failed to get events of user: "+userId+" in the current date range.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
		}		
	}

}