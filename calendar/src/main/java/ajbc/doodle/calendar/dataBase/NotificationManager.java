package ajbc.doodle.calendar.dataBase;

import java.util.PriorityQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.services.NotificationService;
import ajbc.doodle.calendar.services.UserService;

@Component
public class NotificationManager {
	
	@Autowired
	NotificationService notificationService;
	
	@Autowired
	private UserService userService;
	
	private PriorityQueue<Notification> queue;
	private ScheduledThreadPoolExecutor pool;
	private final int NUM_THREADS = 3;
	
	public NotificationManager() 
	{
		this.pool = new ScheduledThreadPoolExecutor(NUM_THREADS);
		this.queue = new PriorityQueue<Notification>((n1,n2) -> n1.getEventToNotify().getStartDateTime()
				.compareTo(n2.getEventToNotify().getStartDateTime()));
	}
	
	@PostConstruct
	public void initQueue() throws DaoException {
		this.queue.addAll(notificationService.getAllNotifications());

	}
	
	public void addNotification(Notification notification) {
		queue.add(notification);
		
		if (notification.getEventToNotify().getStartDateTime()
				.isBefore(queue.peek().getEventToNotify().getStartDateTime())) {

		}
	}
	
	public void run() {
		
	}

}
