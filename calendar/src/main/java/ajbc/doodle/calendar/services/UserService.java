package ajbc.doodle.calendar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.User;

@Component
public class UserService {
	
	@Autowired
	private UserDao userDao;

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
	
}