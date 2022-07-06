package ajbc.doodle.calendar.dataBase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.AddressDao;
import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.EventDao;
import ajbc.doodle.calendar.daos.NotificationDao;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.Address;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.enums.Category;
import ajbc.doodle.calendar.enums.RepeatingType;
import ajbc.doodle.calendar.enums.Unit;

@Component
public class SeedDB {

	@Autowired
	private UserDao userDao;

	@Autowired
	private AddressDao addressDao;
	
	@Autowired
	private EventDao eventDao;
	
	@Autowired
	private NotificationDao notificationDao;

	@EventListener
	public void seed(ContextRefreshedEvent event) throws DaoException {
		seedUsers();
		seedAddresses();
		seedEvents();
		seedNotifications();
	}

	private void seedUsers() throws DaoException {
		List<User> users = userDao.getAllUsers();

		if (users.size() == 0 || users == null) {
			User user1 = new User("Faraj", "Khanjar", "farajkhanjar@gmail.com", LocalDate.of(1993, 6, 28),
					LocalDate.now());

			User user2 = new User("Nasreen", "Madi", "nasreen267@gmail.com", LocalDate.of(2000, 7, 26),
					LocalDate.now());

			User user3 = new User("Wafi", "Khanjar", "wafi10@gmail.com", LocalDate.of(1997, 8, 29), LocalDate.now());

			User user4 = new User("Laila", "Khanjar", "lelo1410@gmail.com", LocalDate.of(1990, 10, 14),
					LocalDate.now());

			User user5 = new User("Doron", "Rainer", "drDodo@gmail.com", LocalDate.of(1991, 5, 5), LocalDate.now());

			List<User> usersList = Arrays.asList(user1, user2, user3, user4, user5);
			usersList.forEach(oneUser -> {
				try {
					userDao.addUser(oneUser);

				} catch (DaoException e) {

					e.printStackTrace();
				}
			});

		}

		// userService.getAllUsers().stream().forEach(System.out::println);

	}

	private void seedAddresses() throws DaoException {

		List<Address> addresses = addressDao.getAllAddresses();

		if (addresses.size() == 0 || addresses == null) {
			Address address1 = new Address("Israel","Tel Aviv","Rothschild",32);
			Address address2 = new Address("Israel","Julis","Barak",91);
			Address address3 = new Address("Israel","Haifa","Neve Sha'anan",15);
			Address address4 = new Address("Israel","Haifa","Grand Kenun",7);
			Address address5 = new Address("Germany","Berlin","Brauereihof",16);
			Address address6 = new Address("Germany","Frankfurt","GALA EVENTS",3);
			Address address7 = new Address("England","London","Tavistock Square",50);
			Address address8 = new Address("England","London","Mohrenstrabe",20);
			
			List<Address> addressesList = Arrays.asList(address1, address2, address3, address4,
					                                    address5, address6, address7, address8);
			addressesList.forEach(oneAddress -> {
				try {
					addressDao.addAddress(oneAddress);

				} catch (DaoException e) {

					e.printStackTrace();
				}
			});

		}

	}

	private void seedEvents() throws DaoException {
		
		List<User> guestsList = new ArrayList<User>();
		guestsList.add(userDao.getUserById(1));
		guestsList.add(userDao.getUserById(2));

		List<Event> eventsList = eventDao.getAllEvents();

		if (eventsList.size() == 0 || eventsList == null) {
			eventDao.addEvent(new Event(1, "AJBC final project",Category.TASK,2,0, LocalDateTime.of(2022, 7, 7, 8, 0),
					LocalDateTime.of(2022, 07, 14, 17, 0), "The topic: Doodle calender API.",
					RepeatingType.NONE, new ArrayList<User>()));
			
			eventDao.addEvent(new Event(2, "Shira & Ran",Category.WEDDING,3,0, LocalDateTime.of(2022, 8, 4, 17, 0),
					LocalDateTime.of(2022, 8, 5, 20, 0),
					"The bridal evening begins at the-Lights Hall in Haifa, the singer begins an hour after the gathering.",
					RepeatingType.NONE, guestsList));
			
		}

	}

	public void seedNotifications() throws DaoException {
		
		List<Notification> notificationsList = notificationDao.getAllNotifications();
		List<Event> eventsList = eventDao.getAllEvents();
		
		if (notificationsList.size() == 0 || notificationsList == null) {
			
		notificationDao.addNotification(
				new Notification("AJBC project", "This is a reminder message for your final programming project.",
						Unit.MINUTES, 10 ,eventsList.get(0).getEventId()));

		notificationDao.addNotification(
				new Notification("The wedding is approaching", "This is a reminder message for your friends wedding.",
						Unit.MINUTES, 10 ,eventsList.get(1).getEventId()));
		
		notificationDao.addNotification(
				new Notification("Wedding gift", "Dont forget to buy a gift for your friends!",
						Unit.MINUTES, 30 ,eventsList.get(1).getEventId()));
		
		notificationDao.addNotification(
				new Notification("Day-off from work", "Reminder to ask for a day off from your boss because of the wedding.",
						Unit.HOURS, 60 ,eventsList.get(1).getEventId()));
		}
	}
}