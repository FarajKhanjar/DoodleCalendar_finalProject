package ajbc.doodle.calendar.managers;

import java.util.List;
import java.util.PriorityQueue;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.services.NotificationService;
import lombok.Setter;

@Setter
@Component
public class NotificationManager {

	@Autowired
	NotificationService notificationService;
	
	private PriorityQueue<Notification> queue;
//	private ScheduledThreadPoolExecutor pool;
//	private final int NUM_THREADS = 3;
	
	private DataManager managerData;
	
	public NotificationManager() 
	{
		//this.pool = new ScheduledThreadPoolExecutor(NUM_THREADS);
		this.queue = new PriorityQueue<Notification>((n1,n2) -> n1.getEventToNotify().getStartDateTime()
				.compareTo(n2.getEventToNotify().getStartDateTime()));
	}
	
	@PostConstruct
	public void initQueue() throws DaoException {
		this.queue.addAll(notificationService.getAllNotifications());

	}
	
	public void addNotification(List<Notification> notifications) {
		for (int i = 0; i < notifications.size(); i++)
			queue.add(notifications.get(i));
	}
	
	public void run() {
		
	}

}