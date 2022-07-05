package ajbc.doodle.calendar.dataBase;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.services.AddressService;
import ajbc.doodle.calendar.services.UserService;

@Component
public class SeedDB {
	
//	@Autowired
//	AddressService addressService;
	
	@Autowired
	UserService userService;
	
	
	//@EventListener
	public void seed(ContextRefreshedEvent event) throws DaoException
	{
		//seedAddresses();
		seedUsers();
		//seedEvents();
		//seedNotifications();
	}
	

	private void seedUsers() throws DaoException
	{
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
		
		List<User> users = Arrays.asList(user1, user2, user3, user4, user5);
		users.forEach(oneUser->{
			try {
				userService.addUser(oneUser);
				
			} catch (DaoException e) {
				
				e.printStackTrace();
			}
		});
		
		userService.getAllUsers().stream().forEach(System.out::println);
		
	}
	
//	private void seedAddresses() {
//		try {
//			addressService.getAllAddresses().stream().forEach(System.out::println);
//		} catch (DaoException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
	
	private void seedEvents()
	{
		
	}
	
	private void seedNotifications()
	{
		
	}

}