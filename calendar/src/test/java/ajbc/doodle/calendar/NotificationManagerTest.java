package ajbc.doodle.calendar;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ajbc.doodle.calendar.managers.NotificationManager;

class NotificationManagerTest {

	private NotificationManager manager;
	
	public NotificationManagerTest() {
		
		this.manager = new NotificationManager();
	}
	
	@Test
	void testConstructor() {
		
		assertNotNull(manager);
		assertNotNull(manager.getQueue());
	}

}
