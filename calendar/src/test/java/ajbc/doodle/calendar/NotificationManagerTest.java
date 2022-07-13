package ajbc.doodle.calendar;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.enums.Unit;
import ajbc.doodle.calendar.managers.NotificationManager;

class NotificationManagerTest {

	private NotificationManager manager;
	
	Notification testNotification1;
	Notification testNotification2;
	
	public NotificationManagerTest() throws DaoException {
		
		this.manager = new NotificationManager();
	}
	
	@Test
	void testConstructor() {
		
		assertNotNull(manager);
		assertNotNull(manager.getQueue());
	}
	
	@Test
	public void addNotificationTest() throws DaoException
	{
		testNotification1 = new Notification("AJBC", "This is a reminder message.",
				Unit.MINUTES, 10 ,null,null);
		
		manager.getQueue().add(testNotification1);
		assertTrue(manager.getQueue().size()==1);	
		assertEquals(testNotification1, manager.getQueue().peek());
	}
	
	@Test
	public void queueTest() throws DaoException
	{
		testNotification2 = new Notification("title2", " message.",
				Unit.MINUTES, 20 ,null,null);
		
		manager.getQueue().add(testNotification2);
		assertEquals(manager.getQueue().size(),1);	
		assertEquals(testNotification2, manager.getQueue().peek());
		
		manager.getQueue().remove(testNotification2);
		assertTrue(manager.getQueue().size()==0);
		assertEquals(testNotification1, manager.getQueue().peek());
	
	}
	
}
