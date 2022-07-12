package ajbc.doodle.calendar.manager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.services.NotificationService;
import lombok.Setter;

@Component
@Setter
public class NotificationManager {
	
	@Autowired
	NotificationService notificationService;
	
	private PriorityBlockingQueue<Notification> notificationQueue; 
	private ExecutorService executorService;
	
	
	public NotificationManager() {
		executorService = Executors.newCachedThreadPool();
		notificationQueue = new PriorityBlockingQueue<Notification>();
	}
	
	public void loadNotificationsIntoQueue() throws DaoException {
		List<Notification> notificationList = notificationService.getAllNotifications();
		notificationList.forEach(n->notificationQueue.add(n));
	}
}
