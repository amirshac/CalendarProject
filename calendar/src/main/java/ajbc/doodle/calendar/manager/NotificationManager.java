package ajbc.doodle.calendar.manager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
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
	private Thread thread;

	private final static int MILISECONDS = 1000;
	private final static int POOLER_THREAD_WAIT_SECONDS = 60;

	public NotificationManager() throws DaoException {
		executorService = Executors.newCachedThreadPool();
		notificationQueue = new PriorityBlockingQueue<Notification>();
	}

	@EventListener
	public void init(ContextRefreshedEvent event) {
		System.out.println("<Notification manager> initializing");
		executorService.execute(new ManagerQueuePoolerThread());
	}

	public void run() {
//
//		while (true) {
//			while (!notificationQueue.isEmpty()) {
//				checkIfThreadNeedsSleep();
//				handleNextNotificationInQueue();
//			}
//		}
	}

	private void checkIfThreadNeedsSleep() {
		Notification nextNotificationInQueue = notificationQueue.peek();
		Duration duration = Duration.between(LocalDateTime.now(), nextNotificationInQueue.getAlertTime());

		// sleep until next notification needs to happen
		if (duration.getSeconds() > 0) {
			System.out.println("<Notification manager> sleeping for seconds: " + duration.getSeconds());
			try {
				Thread.sleep(duration.getSeconds() * MILISECONDS);
			} catch (InterruptedException e) {
				System.out.println("<Notification manager> interrupted sleep");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void handleNextNotificationInQueue() {
		if (notificationQueue.isEmpty())
			return;

		Notification notification = notificationQueue.poll();
	}

	// test: print all notifications in quque
	public void printAllQueue() {
		while (!notificationQueue.isEmpty()) {
			System.out.println(notificationQueue.poll());
		}
	}
	
		
	private class ManagerQueuePoolerThread implements Runnable {
		public ManagerQueuePoolerThread() {
			System.out.println("<Pooler Thread> initialized");
		}

		@Override
		public void run() {
			while (true) {
				loadNotificationsIntoQueue();
				
				System.out.println("<Pooler Thread> Sleeping...");
				try {
					Thread.sleep(POOLER_THREAD_WAIT_SECONDS * MILISECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}

		public synchronized void loadNotificationsIntoQueue() {
			if (!notificationQueue.isEmpty()) {
				System.out.println("<Pooler Thread> pool not empty.");
				return;
			}

			System.out.println("<Pooler Thread> pooling notification queue");
			try {
				List<Notification> notificationList = notificationService.getAllNotifications();
				notificationList.forEach(n -> notificationQueue.add(n));

			} catch (Exception e) {
				System.out.println("<Notification Manager> can't get notifications");
			}
			System.out.println("<Pooler Thread> notification queue pooled");
		}
	}
}
