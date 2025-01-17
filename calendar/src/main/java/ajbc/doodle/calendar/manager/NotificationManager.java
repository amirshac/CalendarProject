package ajbc.doodle.calendar.manager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.PushProp;
import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.services.NotificationService;

@Component

public class NotificationManager {

	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private PushProp pushProp;
	
	private PriorityBlockingQueue<Notification> notificationQueue;
	private ExecutorService executorService;

	private final static int MILISECONDS = 1000;
	private final static int MANAGER_WAIT_SECONDS = 10;
	private final static int MANAGER_INIT_WAIT_SECONDS = 3;
	private final static int MINUTES_TO_POSTPONE_UNSENT_NOTIFICATIONS = 5;
	private final static int EXECUTOR_SHUTDOWN_WAIT_SECONDS = 3;

	private boolean running = true;
	
	public NotificationManager() throws DaoException {
		executorService = Executors.newCachedThreadPool();
		notificationQueue = new PriorityBlockingQueue<Notification>();
	}

	@EventListener
	public void init(ContextRefreshedEvent event) {
		sleep(MANAGER_INIT_WAIT_SECONDS); // wait a bit before running to give server time to boot up
		run();
	}

	public void run() {
		System.out.println("<Notification manager> running");
		while (running) {
			loadNotificationsIntoQueue();

			while (notificationQueue.isEmpty()) {
				sleep(MANAGER_WAIT_SECONDS);
			}

			while (!notificationQueue.isEmpty()) {
				sleepUntilNotificationTime();
				handleNotifications();
			}
			
			sleep(MANAGER_WAIT_SECONDS);
		}
		
		shutdown();
	}

	public void loadNotificationsIntoQueue() {
		try {
			List<Notification> notificationList = notificationService.getAllNotifications();
			notificationList.forEach(n -> notificationQueue.add(n));

		} catch (Exception e) {
			System.out.println("<Notification Manager> can't get notifications");
		}
	}

	private void sleepUntilNotificationTime() {
		Notification nextNotificationInQueue = notificationQueue.peek();
		Duration duration = Duration.between(LocalDateTime.now(), nextNotificationInQueue.getAlertTime());

		// sleep until next notification needs to happen
		if (duration.getSeconds() > 0) {
			System.out.println("<Notification manager> sleeping for seconds: " + duration.getSeconds());
			sleep(duration.getSeconds());
		}
	}

	public void handleNotifications() {
		Notification notification = notificationQueue.poll();

		Event event = notification.getEvent();
		User owner = event.getOwner();

		// if user logged in - send notification through sender thread
		// if user isn't logged in - reschedule notification for later
		if (owner.isLoggedIn()) {
			executorService.execute(new PushNotificationThread(owner.getLoginInfo(), pushProp, notification, notificationService));
		} else {
			
			notification.setAlertTime(LocalDateTime.now().plusMinutes(MINUTES_TO_POSTPONE_UNSENT_NOTIFICATIONS));
			
			try {
				// updates notification in DB with new postponed alert time
				notificationService.updateNotificationWithoutRefresh(notification);
			} catch (DaoException e) {
				e.printStackTrace();
			}
		}
	}

	private void sleep(long seconds) {
		try {
			Thread.sleep(seconds * MILISECONDS);
		} catch (InterruptedException e) {
			System.out.println("<Notification manager> interrupted sleep");
			e.printStackTrace();
		}
	}

	private void shutdown() {
		executorService.shutdownNow();
		try {
			executorService.awaitTermination(EXECUTOR_SHUTDOWN_WAIT_SECONDS, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.out.println("Shutdown interrutped");
		}	
	}
	
	private synchronized void printQueue() {
		System.out.println("<Notification Manager> printing queue:");
		notificationQueue.forEach(n -> System.out.println(n));
	}

}

