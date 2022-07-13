package ajbc.doodle.calendar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.lang.String;
import java.util.List;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.services.EventService;

/**
 * Event Controller that implements the Event-API
 * @author Faraj
 *
 */
@RequestMapping("/events")
@RestController
public class EventController {

	@Autowired
	private EventService eventService;

	/**
	 * This method get all events from the Sql dataBase
	 * @return list of events from the DB. or,
	 * @throws DaoException if there is a error to get 'events' list  or not found.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Event>> getAllEvents() throws DaoException {
		List<Event> events = eventService.getAllEvents();
		return ResponseEntity.ok(events);
	}

	/**
	 * This method add a new event to the DataBase.
	 * @param event: The body of the new event.
	 * @param userId: a key that shows for whom the event belong.
	 * @return a new event, or get a error message if add event is fail.
	 */
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

	/**
	 * This method updates the a event using his id.
	 * @param event: The updated body of the event.
	 * @param userId: key to update and save the updated user of event.
	 * @param userId: key to update and save the updated event.
	 * @return a updated new or a error message if update event is fail.
	 */
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

	/**
	 * This method get the event that have this id number
	 * @param id: the key to search and get the event.
	 * @return the event that have this id, or
	 *        error message if searching for user is fail to get.
	 */
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
	
	/**
	 * This method get all events of an user by user id
	 * @param userId: the key to get the user from DB
	 * @return the events that have this belong to this user id.
	 */
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
	
	/**
	 * This method get the events that have category Id.
	 * @param categoryId: a key to get the events from this type
	 * @return the events by the category id Enum
	 */
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
	
	/**
	 * This method get the events that have category name.
	 * @param categoryName: a key to get the events from this type
	 * @return the events by the category name Enums:
	 *         TASK, WEDDING, VACATION, BIRTHDAY, VOLUNTEERING or GENERAL;
	 */
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
		}
	}
	
	/**
	 * This method get upcoming events of a user (only future events)
	 * @param id: the key to get the user from DB
	 * @return all the upcoming future events for the current user. or,
	 * @throws DaoException if searching for events is fail to get.
	 */
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
	
	/**
	 * This method get events of a user in a range between start date and time to end date and time.
	 * @param userId: the key to get the user from DB
	 * @param startDate: in format of '2022-09-01T00:00'
	 * @param startDate: also in format of '2022-09-01T00:00'
	 * @return all events that have this user in this dateTime range. or,
	 *         get a error message if searching for events is failed.
	 */
	@RequestMapping(method = RequestMethod.GET, path="/userEventsRange/{userId}")
	public ResponseEntity<?> getUserEventsInDateRange(@PathVariable Integer userId, 
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {	
		
		
		if(!checkDateTimeValues(startDate,endDate)) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData("HttpStatus.BAD_REQUEST");
			errorMessage.setMessage("Check ParamValue againe, startDateTime should be before endDateTime");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}
		
		System.out.println("------[Search for events in range dateTime of:]------");
		System.out.println(startDate+"->"+endDate);
		
		List<Event> list;
		try {
			list = eventService.getUserEventsInDateRange(startDate, endDate, userId);
			if (list == null)
				return ResponseEntity.notFound().build();
			return ResponseEntity.ok(list);
			
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("Failed to get events of user: "+userId+" in the current dateTime range.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
		}		
	}
	
	/**
	 * This method get all events in a range between start date and time to end date and time.
	 * @param startDate: in format of '2022-09-01T00:00'
	 * @param startDate: also in format of '2022-09-01T00:00'
	 * @return all events  in this dateTime range. or,
	 *         get a error message if searching for events is failed.
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/allEventsRange")
	public ResponseEntity<?> getAllEventsInDateRange( @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
		
		//TODO throw exception if dateFormat is wrong
		if(!checkDateTimeValues(startDate,endDate)) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData("HttpStatus.BAD_REQUEST");
			errorMessage.setMessage("Check ParamValue againe, startDateTime should be before endDateTime");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}
		
		System.out.println("------[Search for events in range dateTime of:]------");
		System.out.println(startDate+"->"+endDate);
		
		List<Event> list;
		try {
			list = eventService.getAllEventsInDateRange(startDate, endDate);
			if (list == null)
				return ResponseEntity.notFound().build();
			
			return ResponseEntity.ok(list);
		
			} catch (DaoException e) {
				ErrorMessage errorMessage = new ErrorMessage();
				errorMessage.setData(e.getMessage());
				errorMessage.setMessage("Failed to get events in the current dateTime range.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
			}
		
		}
	
	/**
	 * This method get events of a user the next coming num of minutes and hours.
	 * @param userId: the key to get the user from DB
	 * @param hours: number of hours for the event.
	 * @param minutes: number of minutes for the event.
	 * @return all next events by this inputs. or,
	 * @throws DaoException if get a error message if searching for events is failed.
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/upcomingByTime/{userId}")
	public ResponseEntity<?> getNextEventsOfUserByHoursMinutes(@PathVariable Integer userId,
			@RequestParam Integer hours, @RequestParam Integer minutes) throws DaoException {
		
		List<Event> events;
		try {
			events = eventService.getNextEventsOfUserByHoursMinutes(hours, minutes, userId);
			if (events == null)
				return ResponseEntity.notFound().build();
			
			return ResponseEntity.ok(events);
			
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("Failed to get events in the current by next num of hours and minutes.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
		}

	}
		
	/**
	 * This is a handle method for check if the entering of the date in Postman is correct.
	 * @return false or true.
	 */
	private boolean checkDateTimeValues(LocalDateTime startDate, LocalDateTime endDate) {
		if (startDate.compareTo(endDate) > 0)
			return false;
		return true;
	}
	
	/**
	 * This method get all events of user by category name.
	 * @param userId: the current user
	 * @param categoryName: the type/ category of event that the user interested to get.
	 * @return all events of user in the category. or,
	 *         get a error message if searching for events is failed.
	 *         The Category Enums:
	 *         TASK, WEDDING, VACATION, BIRTHDAY, VOLUNTEERING or GENERAL;
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/userByCategoryName/{userId}/{categoryName}")
	public ResponseEntity<?> getUserEventsByCategoryName(@PathVariable Integer userId, @PathVariable String categoryName) {
		
		List<Event> eventsOfCategory;
		try {
			eventsOfCategory = eventService.getUserEventsByCategoryName(userId,categoryName.toUpperCase());
			return ResponseEntity.ok(eventsOfCategory);
			
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("Failed to get events By this category");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMessage);
		}
	}
	
	/**
	 * This method do a soft delete of the a event using his id.
	 * @param eventId: the key to get the event from DB
	 * @return updated event that have a value = 1 in failed = inActive. or,
	 * @throws DaoException if deleting softly of event is fail.
	 */
	@RequestMapping(method = RequestMethod.PUT, path="/softDelete/{eventId}")
	public ResponseEntity<?> deleteEventSoftly(@PathVariable Integer eventId) {
		
		try {
			eventService.deleteEventSoftly(eventId);
			Event event = eventService.getEventById(eventId);
			return ResponseEntity.status(HttpStatus.OK).body(event);
			
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("Failed to do soft delete for this event");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMessage);
		}
	}
	
	/**
	 * This method do a soft delete of a list of events.
	 * @param eventList: the key to get the event from DB
	 * @return updated event that have a value = 1 in failed = inActive. or,
	 * @throws DaoException if deleting softly of events list is fail.
	 */
	@RequestMapping(method = RequestMethod.PUT, path="/softDeleteList")
	public ResponseEntity<?> deleteEventsListSoftly(@RequestBody List<Integer> eventList) {
		
		try {
			eventService.deleteEventsListSoftly(eventList);
			return ResponseEntity.status(HttpStatus.OK).body("Soft Delete done!");
			
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("Failed to do soft delete for this event list");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMessage);
		}
	}
	
	/**
	 * This method do a hard delete of the a event using his id.
	 * @param eventId: the key to get the event from DB
	 * @return a message that the delete done or not, and delete event totally from DB. or,
	 * @throws DaoException if deleting hardly of event is fail.
	 */
	@RequestMapping(method = RequestMethod.DELETE, path="/hardDelete/{eventId}")
	public ResponseEntity<?> deleteEventHardly(@PathVariable Integer eventId) {
		
		try {
			eventService.deleteEventHardly(eventId);
			return ResponseEntity.status(HttpStatus.OK).body("Event deleted!");
			
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("Failed to do hard delete event.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMessage);
		}
	}

	/**
	 * This method do a hard delete of a list of events.
	 * @param eventList: the key to get the event from DB
	 * @return a message that the delete done or not, and delete events list totally from DB. or,
	 * @throws DaoException if deleting hardly of events list is fail.
	 */
	@RequestMapping(method = RequestMethod.DELETE, path="/hardDeleteList")
	public ResponseEntity<?> deleteEventsListHardly(@RequestBody List<Integer> eventsList) {
		
		try {
			eventService.deleteEventsListHardly(eventsList);
			return ResponseEntity.status(HttpStatus.OK).body("Events list deleted!");
			
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("Failed to do hard delete events list");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMessage);
		}
	}
}