package ajbc.doodle.calendar.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.entities.Notification;
import lombok.Setter;

@Component
@Setter
public class NotificationManager {
	
	private PriorityBlockingQueue<Notification> notifications; 
	private ExecutorService executorService;
	
	
	public NotificationManager() {
		executorService = Executors.newCachedThreadPool();
		notifications = new PriorityBlockingQueue<Notification>();
		
	}
}
