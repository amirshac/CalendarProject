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
	private Thread poolThread;
	private Thread handlerThread;

	private final static int MILISECONDS = 1000;
	private final static int POOLER_THREAD_WAIT_SECONDS = 10;
	private final static int HANDLER_THREAD_WAIT_SECONDS = 10;

	public NotificationManager() throws DaoException {
		executorService = Executors.newCachedThreadPool();
		notificationQueue = new PriorityBlockingQueue<Notification>();
	}

	@EventListener
	public void init(ContextRefreshedEvent event) {
		System.out.println("<Notification manager> initializing");
		poolThread = new Thread(new ManagerQueuePoolerThread());
		handlerThread = new Thread(new ManagerQueueHandlerThread());
		
		executorService.execute(poolThread);
		
		int milisUntilNextThread = 1000;
		try {
			Thread.sleep(milisUntilNextThread);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		executorService.execute(handlerThread);
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

	/**
	 * This thread pools notifications from DB into priorityqueue
	 * 
	 */
	private class ManagerQueuePoolerThread implements Runnable {
		public ManagerQueuePoolerThread() {
			System.out.println("<Pooler Thread> initialized");
		}

		@Override
		public void run() {
			while (true) {
				loadNotificationsIntoQueue();

				System.out.println("<Pooler Thread> Sleeping for seconds: " + POOLER_THREAD_WAIT_SECONDS);
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

	
	
	private class ManagerQueueHandlerThread implements Runnable {
		private long secondsToSleep = HANDLER_THREAD_WAIT_SECONDS;
		
		public ManagerQueueHandlerThread() {
			System.out.println("<Handler Thread> initialized");
		}
		
		@Override
		public void run() {
			while (true) {
				handleNotifications();
				
				if (secondsToSleep < 0)
					secondsToSleep = HANDLER_THREAD_WAIT_SECONDS;
				
				System.out.println("<Handler Thread> Sleeping " + secondsToSleep + " seconds.");
				try {
					Thread.sleep(secondsToSleep * MILISECONDS);
				} catch (InterruptedException e) {
					System.out.println("<Handler Thread> interrupted");
				}
			}
		}

		public synchronized void handleNotifications() {
			if (notificationQueue.isEmpty()) {
				System.out.println("<Handler Thread> notification queue empty. waiting...");
				return;
			}

			System.out.println("<Handler Thread> pooling notification queue");
			if (doesThreadNeedSleep())
				return;
			
			testPrintQueue();
			
//			try {
//
//
//			} catch (Exception e) {
//				System.out.println("<Handler Thread> can't get notifications");
//			}
			
			//System.out.println("<Pooler Thread> notification queue pooled");
		}
		
		// Checks how long until the next message and set sleep for that duration
		private boolean doesThreadNeedSleep() {
			Notification nextNotificationInQueue = notificationQueue.peek();
			Duration duration = Duration.between(LocalDateTime.now(), nextNotificationInQueue.getAlertTime());

			secondsToSleep = duration.getSeconds();
			System.out.println("<Handler Thread> Next notification is in seconds: " + secondsToSleep);
			
		    return (secondsToSleep > 0);
		}
		
		private synchronized void testPrintQueue() {
			System.out.println("<Handler Thread> printing queue:");
			notificationQueue.forEach(n -> System.out.println(n));
		}

	}

}
