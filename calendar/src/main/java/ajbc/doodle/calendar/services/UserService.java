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
import ajbc.doodle.calendar.entities.webpush.Subscription;

@Component
public class UserService {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private EventDao eventDao;

	// CRUD
	public List<User> getAllUsers() throws DaoException {
		return userDao.getAllUsers();
	}
	
	public void addUser(User user) throws DaoException {
		userDao.addUser(user);
	}
	
	public void updateUser(User user) throws DaoException {
		userDao.updateUser(user);
	}

	// Queries
	public User getUserById(Integer userId) throws DaoException {
		return userDao.getUserById(userId);
	}

	public User getUserByEmail(String email) throws DaoException {
		return userDao.getUserByEmail(email);
	}
	
	public void deleteUserSoftly(User user) throws DaoException {
		userDao.deleteUserSoftly(user);	
	}
	
	public void deleteUserHardly(User user) throws DaoException {
		userDao.deleteUserHardly(user);	
	}
	
	@Transactional
	public List<User> getEventUsers(Integer eventId) throws DaoException {
		Event event = eventDao.getEventById(eventId);
		return event.getEventGuests().stream().collect(Collectors.toList());
	}
	
	// Login + Logout

	public void userLogin(String email) throws DaoException {
		
		// if user exists - check by email
		User user = getUserByEmail(email);
		System.out.println("------["+user.getFirstName()+" "
		                   +user.getLastName()+" subscribe]------");
		// 1 set login flag to true
		user.setUserOnline(1);
		System.out.println("Status user online: "+user.getUserOnline());
		
		// 2 save subscription data
		//user.setEndPoint(subscription.getEndpoint());
		//user.setP256dh(subscription.getKeys().getP256dh());
		//user.setAuth(subscription.getKeys().getAuth());
		
		// 3 update new user
		userDao.updateUser(user);
		
	}
	
	public void userLogout(String email) throws DaoException {
		
		User user = getUserByEmail(email);
		//user.setEndPoint(null);
		user.setUserOnline(0);
		System.out.println("------["+user.getFirstName()+" "
		                   +user.getLastName()+" unsubscribe]------");
		System.out.println("Status user online: "+user.getUserOnline());
		userDao.updateUser(user);
		
	}	
	
}