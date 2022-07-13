package ajbc.doodle.calendar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.SubscriptionInfo;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.webpush.Subscription;
import ajbc.doodle.calendar.entities.webpush.SubscriptionEndpoint;
import ajbc.doodle.calendar.services.UserService;

/**
 * User Controller that implements the User-API
 * @author Faraj
 *
 */
@RequestMapping("/users")
@RestController
public class UserController {

	@Autowired
	private UserService userService;

	/**
	 * This method get all users from the Sql dataBase
	 * @return list of users from the DB. or,
	 * @throws DaoException if there is a error to get 'allUsers' list  or not found.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<User>> getAllUsers() throws DaoException {
		List<User> allUsers = userService.getAllUsers();
		if (allUsers == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		return ResponseEntity.ok(allUsers);
	}

	/**
	 * This method add a new user to the DataBase.
	 * @param user: The body of the new user.
	 * @return a new user, or get a error message if add user is fail.
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addUser(@RequestBody User user) {
		try {
			userService.addUser(user);
			user = userService.getUserById(user.getUserId());
			return ResponseEntity.status(HttpStatus.CREATED).body(user);
			
		} catch (DaoException e) {
			ErrorMessage errorMsg = new ErrorMessage();
			errorMsg.setData(e.getMessage());
			errorMsg.setMessage("Failed to add user to DB");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMsg);
		}
	}

	/**
	 * This method updates the a user using his id.
	 * @param user: The updated body of the user.
	 * @param id: key to update and save the updated user.
	 * @return a updated new or a error message if update user is fail.
	 */
	@RequestMapping(method = RequestMethod.PUT, path = "/byId/{id}")
	public ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable Integer id) {
		try {
			user.setUserId(id);
			userService.updateUser(user);
			user = userService.getUserById(id);
			return ResponseEntity.status(HttpStatus.OK).body(user);
			
		} catch (DaoException e) {
			ErrorMessage errorMsg = new ErrorMessage();
			errorMsg.setData(e.getMessage());
			errorMsg.setMessage("Failed to update this user");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMsg);
		}
	}

	/**
	 * This method get the user that have this id number
	 * @param id: the key to search and get the user.
	 * @return the user that have this id, or
	 * @throws DaoException if searching for user is fail to get.
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/byId/{id}")
	public ResponseEntity<?> getUserById(@PathVariable Integer id) throws DaoException {
		try {
			User user = userService.getUserById(id);
			return ResponseEntity.ok(user);
			
		} catch (DaoException e) {
			ErrorMessage errorMsg = new ErrorMessage();
			errorMsg.setData(e.getMessage());
			errorMsg.setMessage("Failed to get user By this Id");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMsg);
		}

	}

	/**
	 * This method get the user that have this email
	 * @param email: the key to search and get the user.
	 * @return the user that have this email, or
	 * @throws DaoException if searching for user is fail to get.
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/byEmail/{email}")
	public ResponseEntity<?> getUserByEmail(@PathVariable String email) throws DaoException {
		try {
			User user = userService.getUserByEmail(email);
			return ResponseEntity.ok(user);
			
		} catch (DaoException e) {
			ErrorMessage errorMsg = new ErrorMessage();
			errorMsg.setData(e.getMessage());
			errorMsg.setMessage("Failed to get user By this email");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMsg);
		}
	}
	
	/**
	 * This method do a soft delete of the a user using his id.
	 * @param id: the key to get the user from DB
	 * @return updated user that have a value = 1 in failed = inActive. or,
	 * @throws DaoException if deleting softly of user is fail.
	 */
	@RequestMapping(method = RequestMethod.DELETE,path = "/softly/{id}")
	public ResponseEntity<?> deleteUserSoftly(@PathVariable Integer id) throws DaoException {
		try {
			User user = userService.getUserById(id);
			userService.deleteUserSoftly(user);
			user = userService.getUserById(id);
			return ResponseEntity.status(HttpStatus.OK).body(user);
			
		} catch (DaoException e) {
			ErrorMessage errorMsg = new ErrorMessage();
			errorMsg.setData(e.getMessage());
			errorMsg.setMessage("Failed to delete this user softly");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMsg);
		}
		
	}
	
	/**
	 * This method do a hard delete of the a user using his id.
	 * @param id: the key to get the user from DB
	 * @return a message that the delete done or not, and delete user totally from DB. or,
	 * @throws DaoException if deleting hardly of user is fail.
	 */
	@RequestMapping(method = RequestMethod.DELETE,path = "/hardly/{id}")
	public ResponseEntity<?> deleteUserHardly(@PathVariable Integer id) throws DaoException {
		// TODO fix it
		try {
			User user = userService.getUserById(id);
			userService.deleteUserHardly(user);
			user = userService.getUserById(id);
			return ResponseEntity.status(HttpStatus.OK).body(user);
			
		} catch (DaoException e) {
			ErrorMessage errorMsg = new ErrorMessage();
			errorMsg.setData(e.getMessage());
			errorMsg.setMessage("Failed to delete this user hardly");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMsg);
		}
		
	}
	
	/**
	 * This method get all users of an event by event id
	 * @param id: the key to get the event from DB
	 * @return the users that have this event id.
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/byEventId/{id}")
	public ResponseEntity<?> getEventUsers(@PathVariable Integer id)  {
		try {
			List<User> users = userService.getEventUsers(id);
			return ResponseEntity.ok(users);
			
		} catch (DaoException e) {
			ErrorMessage errorMsg = new ErrorMessage();
			errorMsg.setData(e.getMessage());
			errorMsg.setMessage("Failed to get users of this event.");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMsg);
			//TODO fix null list return
		}
	}
	
	/**
	 * This method get all user that have an event between start date and time to end date and time.
	 * @param startDate: in format of '2022-09-01T00:00'
	 * @param endDate: also in format of '2022-09-01T00:00'
	 * @return all users that have events in this dateTime range. or,
	 *         get a error message if searching for user is fail to get.
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/eventsRange")
	public ResponseEntity<?> getAllUsersInRangeDateEvent( @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
		
		//TODO throw exception if dateFormat is wrong
		if(!checkDateTimeValues(startDate,endDate)) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData("HttpStatus.BAD_REQUEST");
			errorMessage.setMessage("Check ParamValue againe, startDateTime should be before endDateTime");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}
		
		System.out.println("------[Search for users in range dateTime of:]------");
		System.out.println(startDate+"->"+endDate);
		
		List<User> list;
		try {
			list = userService.getAllUsersInRangeDateEvent(startDate, endDate);
			if (list == null)
				return ResponseEntity.notFound().build();
			
			return ResponseEntity.ok(list);
		
			} catch (DaoException e) {
				ErrorMessage errorMessage = new ErrorMessage();
				errorMessage.setData(e.getMessage());
				errorMessage.setMessage("Failed to get users in the current dateTime range.");
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
	 * This method get all users inActive or not
	 * @param userId: the key to get the user from DB
	 * @return all users that inActivity
	 */
	@RequestMapping(method = RequestMethod.PUT, path="/inActive/{userId}")
	public ResponseEntity<?> inActiveUser(@PathVariable Integer userId) {

		try {
			userService.inActiveUser(userId);
			User user = userService.getUserById(userId);
			return ResponseEntity.status(HttpStatus.OK).body(user);
			
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("Failed to make user inActive in DB");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMessage);
		}
	}
	
	/**
	 * This method is the LogIn of user.
	 * @param subscription: the data that sent from the browser and save in Sql table.
	 * @param email: the user email
	 * @return success message to login. or error message if failed.
	 */
	@PostMapping("/subscribe/{email}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> subscribe(@RequestBody Subscription subscription,
			@PathVariable(required = false) String email) {

		try {

			System.out.println("user email: "+email);
			
			String publicKey = subscription.getKeys().getP256dh();
			String authKey = subscription.getKeys().getAuth();
			String endPoint = subscription.getEndpoint();

			System.out.println("------[subscription Info]------");
			System.out.println("publicKey= "+publicKey);
			System.out.println("authKey= "+authKey);
			System.out.println("endPoint= "+endPoint);
			
			User user = userService.getUserByEmail(email);
			SubscriptionInfo currentSubscriptionInfo = new SubscriptionInfo(publicKey, authKey, endPoint);
			user.setSubscriptionInfo(currentSubscriptionInfo);
			userService.updateUser(user);
			userService.userLogin(email);
			return ResponseEntity.ok("User online! login with this email: "+email);
			
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("Failed to login with this email "+email);
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMessage);
		}
		
	}
	
	/**
	 * This method is the LogOut of user.
	 * @param subscription: the end point from browser
	 * @param email: the user email
	 * @return success message to logout. or error message if failed.
	 */
	@PostMapping("/unsubscribe/{email}")
	public ResponseEntity<?> unsubscribe(@RequestBody SubscriptionEndpoint subscription,
			@PathVariable(required = false) String email) {

		try {

			userService.userLogout(email);
			return ResponseEntity.ok("User offline! logout with this email: "+email);
			
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("Failed to logout with this email "+email);
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMessage);
		}
	}
	
	/**
	 * This method check if a user is currently subscribed to push messages by checking his end point
	 * @param subscription: the end point from browser
	 * @return true if a user is subscribed, false if not
	 * @throws DaoException
	 */
	@PostMapping("/isSubscribed")
	public boolean isSubscribed(@RequestBody SubscriptionEndpoint subscription) throws DaoException {
		List<User> allUsers = userService.getAllUsers();
		for(User oneUser : allUsers)
			if(oneUser.getSubscriptionInfo().getEndpoint().equals(subscription.getEndpoint()))
					return true;
			return false;
	}
	
}