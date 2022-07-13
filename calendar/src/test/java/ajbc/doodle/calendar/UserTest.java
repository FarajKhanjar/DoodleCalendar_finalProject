package ajbc.doodle.calendar;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.enums.Category;
import ajbc.doodle.calendar.enums.RepeatingType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

class UserTest 
{
	private final Integer USER_ID = null;
	
	private final String FIRST_NAME = "Faraj";
	private final String LAST_NAME = "Khanjar";
	private final String EMAIL = "farajkhanjar@gmail.com";

	private final LocalDate BIRTH_DATE = LocalDate.of(1993, 6, 28);
	private final LocalDate JOIN_DATE = LocalDate.of(1993, 7, 6);

	private final Integer INACTIVE = 0;
	private final Integer USER_ONLINE = null;

	private User user;

	public UserTest() {
		user = new User(FIRST_NAME, LAST_NAME, EMAIL, BIRTH_DATE, JOIN_DATE);
	}

	@Test
	void ConstructorTest() {

		assertNotNull(user);
		assertNull(user.getUserId());

		assertTrue(user.getEvents().isEmpty());
		assertNotNull(user.getEvents());

		assertEquals(FIRST_NAME, user.getFirstName());
		assertEquals(LAST_NAME, user.getLastName());
		assertEquals(EMAIL, user.getEmail());

		assertEquals(BIRTH_DATE, user.getBirthDate());
		assertEquals(JOIN_DATE, user.getJoinDate());

		assertEquals(INACTIVE, user.getInActive());
		assertEquals(USER_ONLINE, user.getUserOnline());

		assertNull(user.getSubscriptionInfo());

	}

	@Test
	void getEmailTest() {
		assertEquals(EMAIL, user.getEmail());
	}

	@Test
	void setEmailTest() {
		String testEmail = "nasreen276@gmail.com";
		user.setEmail(testEmail);
		assertEquals(testEmail, user.getEmail());
	}

	@Test
	void getUserIdTest() {
		assertEquals(USER_ID, user.getUserId());
	}

	@Test
	void setUserIdTest() {
		Integer testId = 2;
		user.setUserId(testId);
		assertEquals(testId, user.getUserId());
	}

	@Test
	void getFirstNameTest() {
		assertEquals(FIRST_NAME, user.getFirstName());
	}

	@Test
	void setFirstNameTest() {
		String testName = "Nasreen";
		user.setFirstName(testName);
		assertEquals(testName, user.getFirstName());
	}

	@Test
	void getLastNameTest() {
		assertEquals(LAST_NAME, user.getLastName());
	}

	@Test
	void setLastNameTest() {
		String testName = "Madi";
		user.setLastName(testName);
		assertEquals(testName, user.getLastName());
	}

	@Test
	void getBirthDateTest() {
		assertEquals(BIRTH_DATE, user.getBirthDate());
	}

	@Test
	void setBirthDateTest() {
		LocalDate testDate = LocalDate.of(2000, 7, 6);
		user.setBirthDate(testDate);
		assertEquals(testDate, user.getBirthDate());
	}

	@Test
	void getJoinDateTest() {
		assertEquals(JOIN_DATE, user.getJoinDate());
	}

	@Test
	void setJoinDateTest() {
		LocalDate testDate = LocalDate.of(2022, 1, 1);
		user.setBirthDate(testDate);
		assertEquals(testDate, user.getBirthDate());
	}

	@Test
	void getInactiveTest() {
		assertEquals(INACTIVE, user.getInActive());
	}

	@Test
	void setInActiveTest() {
		Integer testActivity = 1;
		user.setInActive(testActivity);
		assertEquals(testActivity, user.getInActive());
	}

	@Test
	void userOnlineTest() {
		assertEquals(USER_ONLINE, user.getUserOnline());
	}

	@Test
	void setUserOnlineTest() {
		Integer testLogin = 1;
		user.setUserOnline(testLogin);
		assertEquals(testLogin, user.getUserOnline());
	}
	
	@Test
	void setEventsTest()
	{
		User user = new User();
		Event event_1 = new Event();
		event_1.setEventId(1);
		event_1.setTitle("Birthday!!");
		event_1.setCategory(Category.BIRTHDAY);
		event_1.setAddressId(7);
		event_1.setIsAllDay(0);
		event_1.setStartDateTime(LocalDateTime.now());
		event_1.setEndDateTime(LocalDateTime.now().plusHours(2));
		event_1.setDescription("its gonna be a good time");
		event_1.setRepeatingType(RepeatingType.DAILY);
		event_1.setInActive(0);
		
		Set<Event> events = Set.of(event_1);
		user.setEvents(events);
		
		Set<Event> user_Events = user.getEvents();
		assertFalse(user_Events.isEmpty());
		assertTrue(user_Events.size() == 1);
		
		assertTrue(user_Events.contains(event_1));
		
	}
}
