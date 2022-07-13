package ajbc.doodle.calendar.managers;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.EventDao;
import ajbc.doodle.calendar.daos.NotificationDao;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.enums.Unit;
import ajbc.doodle.calendar.services.NotificationService;
import lombok.Setter;

@Setter
@Component
public class NotificationManager {

	@Autowired
	NotificationService notificationService;

	@Autowired
	private NotificationDao notificationDao;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private EventDao eventDao;

	private PriorityQueue<Notification> queue;

	private DataManager dataManager;
	private ExecutorService executorService;
	private Thread thread;

	public NotificationManager() {

		this.queue = new PriorityQueue<Notification>((n1, n2) -> calculateNotificationTime(n1)
				.compareTo(calculateNotificationTime(n2)));
	}

	public void initNotificationToQueue(List<Notification> notifications) {
		executorService = Executors.newCachedThreadPool();
		queue.addAll(notifications.stream().filter(
				oneNotification -> oneNotification.getEventToNotify().getStartDateTime().isAfter(LocalDateTime.now())
						&& oneNotification.getInActive() == 0)
				.toList());
	}


	@Transactional
	public void run() throws DaoException, InterruptedException {
		thread = new Thread(() -> {
			try {
				User user;
				Notification notification;

				System.out.println("App is run");

				while (queue.isEmpty() == false) {
					notification = queue.poll();
					user = userDao.getUserById(notification.getUserId());

					if (user.getUserOnline() == 1 && notification.getIsSent() == 0) {
						executorService.execute(new PushManager(dataManager, user, notification));
						//isSentNotification(notification);
					}
					
				}
				Thread.sleep(3000);
				System.out.println("Notification Quere is null - No such user online.");
				
			} catch (DaoException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		});

		thread.start();
	}

	public void updateNotification(Notification notification) throws DaoException {
		if (thread.isAlive())
			thread.interrupt();
		
		Iterator<Notification> it = queue.iterator();
		while (it.hasNext()) {
			if (it.next().getNotificationId() == notification.getNotificationId()) {
				it.remove();
				queue.add(notification);
				break;
			}
		}
		try {
			run();
		} catch (DaoException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void updateListOfNotifications(List<Notification> notifications) {
		notifications.forEach(notification -> {
			try {
				updateNotification(notification);
			} catch (DaoException e) {
				e.printStackTrace();
			}
		});
	}


	public void makeNotificationsInActive(Notification notification) throws DaoException {
		notification = notificationDao.getNotificationById(notification.getNotificationId());
		notification.setIsSent(1);
		notificationDao.updateNotification(notification);
		
		notification = notificationDao.getNotificationById(notification.getNotificationId());
		notification.setInActive(1);
		notificationDao.updateNotification(notification);
	}
	
	public LocalDateTime calculateNotificationTime(Notification notification) {
		Event event;
		try {
			event = eventDao.getEventById(notification.getEventId());
			LocalDateTime currentTime;
			if (notification.getUnit() == Unit.MINUTES)
				currentTime = event.getStartDateTime().minusMinutes(notification.getQuantity());
			else
				currentTime = event.getStartDateTime().minusHours(notification.getQuantity());

			return currentTime;

		} catch (DaoException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void deleteNotification(Integer notificationId) {
		if (thread.isAlive())
			thread.interrupt();
	
		Iterator<Notification> it = queue.iterator();
		while (it.hasNext()) {
			if (it.next().getNotificationId() == notificationId) {
				it.remove();
				
				try {
					queue.add(notificationDao.getNotificationById(notificationId));
					
				} catch (DaoException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		
		try {
			run();
		} catch (DaoException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		

	}
}