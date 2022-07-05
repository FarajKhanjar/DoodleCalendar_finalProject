package ajbc.doodle.calendar.dataBase;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.User;

@Component
public class SeedDB {
	
	@Autowired
	private UserDao userDao;

	@EventListener
	public void seed(ContextRefreshedEvent event) throws DaoException
	{
		seedUsers();
		//seedAddresses();
		//seedEvents();
		//seedNotifications();
	}
	

	private void seedUsers() throws DaoException
	{
		List<User> users = userDao.getAllUsers();
		
		if (users == null || users.size() == 0) {
			User user1 = new User("Faraj", "Khanjar", "farajkhanjar@gmail.com", 
					LocalDate.of(1993, 6, 28), LocalDate.now());
			
			User user2 = new User("Nasreen", "Madi", "nasreen267@gmail.com", 
					LocalDate.of(2000, 7, 26), LocalDate.now());
			
			User user3 = new User("Wafi", "Khanjar", "wafi10@gmail.com",
					LocalDate.of(1997, 8, 29), LocalDate.now());
			
			User user4 = new User("Laila", "Khanjar", "lelo1410@gmail.com",
					LocalDate.of(1990, 10, 14), LocalDate.now());
			
			User user5 = new User("Doron", "Rainer", "drDodo@gmail.com",
					LocalDate.of(1991, 5, 5), LocalDate.now());
			
			List<User> usersList = Arrays.asList(user1, user2, user3, user4, user5);
			usersList.forEach(oneUser->{
				try {
					userDao.addUser(oneUser);
					
				} catch (DaoException e) {
					
					e.printStackTrace();
				}
			});
		
		}
		
		//userService.getAllUsers().stream().forEach(System.out::println);
		
	}
	
	private void seedAddresses() {

	}
	
	private void seedEvents()
	{
		
	}
	
	private void seedNotifications()
	{
		
	}

}