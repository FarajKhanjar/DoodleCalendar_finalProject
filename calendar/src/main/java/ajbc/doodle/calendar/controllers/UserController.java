package ajbc.doodle.calendar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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

@RequestMapping("/users")
@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<User>> getAllUsers() throws DaoException {
		List<User> allUsers = userService.getAllUsers();
		if (allUsers == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		return ResponseEntity.ok(allUsers);
	}

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


}