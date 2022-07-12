package ajbc.doodle.calendar.managers;

import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.services.NotificationService;
import lombok.Setter;

@Setter
@Component
public class NotificationManager {

	@Autowired
	NotificationService notificationService;
	
	@Autowired
	private UserDao userDao;
	
	private PriorityQueue<Notification> queue;
//	private ScheduledThreadPoolExecutor pool;
//	private final int NUM_THREADS = 3;
	
	private DataManager dataManager;
	private ExecutorService executorService;
	
	public NotificationManager() {
		//this.pool = new ScheduledThreadPoolExecutor(NUM_THREADS);
		this.queue = new PriorityQueue<Notification>((n1,n2) -> n1.getEventToNotify().getStartDateTime()
				.compareTo(n2.getEventToNotify().getStartDateTime()));
	}
	
	public void addNotification(List<Notification> notifications) {
		executorService = Executors.newCachedThreadPool();
		for (int i = 0; i < notifications.size(); i++)
			queue.add(notifications.get(i));
	}
	
	public void run() throws DaoException, InterruptedException  {
		User user;
		Notification notification ;

		System.out.println("App is run");
		
		while(queue.isEmpty()==false) 
		{
			notification = queue.poll();
			user = userDao.getUserById(notification.getUserId());
			
			if(user.getUserOnline()==1)
				executorService.execute(new PushManager(dataManager, user, notification));
			Thread.sleep(3000);

		}
		
		System.out.println("Notification Quere is null - No such user online.");
	}

}