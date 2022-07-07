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
		List<Event> events = eventService.getAllEvents();
		return ResponseEntity.ok(events);
	}

	@RequestMapping(method = RequestMethod.POST, path = "/{id}")
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

	@RequestMapping(method = RequestMethod.PUT, path = "/{id}")
	public ResponseEntity<?> updateUser(@RequestBody Event event, @RequestParam Integer userId ,@RequestParam Integer eventId) {
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
	
	@RequestMapping(method = RequestMethod.GET, path = "/byUserId/{id}")
	public ResponseEntity<List<Event>> getUserEvents(@PathVariable Integer userId) throws DaoException {
		try {
			List<Event> events = eventService.getUserEvents(userId);
			return ResponseEntity.ok(events);
			
		} catch (DaoException e) {
			ErrorMessage errorMsg = new ErrorMessage();
			errorMsg.setData(e.getMessage());
			errorMsg.setMessage("Failed to get user events");
			return ResponseEntity.status(HttpStatus.valueOf(500)).build();
		}

	}
	
}