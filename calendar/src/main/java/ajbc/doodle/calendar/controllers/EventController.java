package ajbc.doodle.calendar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.services.EventService;


@RequestMapping("/events")
@RestController
public class EventController {

	@Autowired
	private EventService eventService;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Event>> getAllEvents() throws DaoException {
		List<Event> allEvents = eventService.getAllEvents();
		if (allEvents == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		return ResponseEntity.ok(allEvents);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> AddEvent(@RequestBody Event event, @PathVariable Integer userId) {
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

//	@RequestMapping(method = RequestMethod.PUT, path = "/byId/{id}")
//	public ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable Integer id) {
//		try {
//			user.setUserId(id);
//			userService.updateUser(user);
//			user = userService.getUserById(id);
//			return ResponseEntity.status(HttpStatus.OK).body(user);
//			
//		} catch (DaoException e) {
//			ErrorMessage errorMsg = new ErrorMessage();
//			errorMsg.setData(e.getMessage());
//			errorMsg.setMessage("Failed to update this user");
//			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMsg);
//		}
//	}

	@RequestMapping(method = RequestMethod.GET, path = "/byId/{id}")
	public ResponseEntity<?> getEventById(@PathVariable Integer id) throws DaoException {
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
	
}