package ajbc.doodle.calendar.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.enums.ReminderUnit;
import ajbc.doodle.calendar.enums.RepeatingOptions;
import ajbc.doodle.calendar.services.EventService;
import ajbc.doodle.calendar.services.NotificationService;
import ajbc.doodle.calendar.services.UserService;

@Component
public class SeedDB {
	
	@Autowired
	private UserService userService;
	@Autowired
	private EventService eventService;
	@Autowired
	private NotificationService notificationService;
	
	@EventListener
	public void seed(ContextRefreshedEvent event) {
		boolean seedDb = true;
		
		if (!seedDb) {
			System.out.println("seedDB -> Skipping database seed");
			return;
		}
		
		try {
			seedUsersTable();
			seedEventsTable();
			seedNotificationsTable();
		}catch (DaoException e) {
			System.out.println(e);
		}
	}
	
	public void seedUsersTable() throws DaoException {
		
		System.out.println("seeding users");

		List<User> users = new ArrayList<>();
		
		users.add( new User("John", "Doe", "john.doe@gmail.com", LocalDate.of(2022, 01, 01), LocalDate.of(2022, 01, 01)) );
		users.add( new User("Mary", "Jane", "mary.jane@gmail.com", LocalDate.of(2022, 01, 01), LocalDate.of(2022, 01, 01)) );
		users.add( new User("Bill", "Gates", "bill.gates@gmail.com", LocalDate.of(2022, 01, 01), LocalDate.of(2022, 01, 01)) );
		
		users.forEach(user ->{
			System.out.println(user);
			try {
				userService.addUser(user);
			} catch (DaoException e) {
				System.out.println(e);
			}
		});
		
		User user;
		Event event;
		Notification notification;
		
		user = userService.getUserById(1);
		
		event = new Event(0, 1, "Johns Birthday", LocalDateTime.of(2022, 7, 6, 10, 00), LocalDateTime.of(2022, 07, 06, 10, 30), false, "john street", "Its john's birthday", RepeatingOptions.NONE, false, null, null);
		
		notification = new Notification(1, "Johns birthday notification", event.getDescription(), event.getStarting(), ReminderUnit.MINUTES, 30);
		notification.refresh();
		
		event.addNotification(notification);
		user.addEvent(event);
		
		System.out.println(user);
			
		userService.updateUser(user);
		
		System.out.println("users seeded");
	}
	
	public void seedEventsTable() throws DaoException{
		System.out.println("seeding events");
		
		List<Event> events = new ArrayList<>();
		events.add( new Event(0, 1, "JohnsEvent", LocalDateTime.of(2022, 7, 6, 10, 00), LocalDateTime.of(2022, 07, 06, 10, 30), false, "address1", "desc1", RepeatingOptions.NONE, false, null, null) );
		events.add( new Event(0, 2, "MarysEvent", LocalDateTime.of(2022, 7, 7, 11, 00), LocalDateTime.of(2022, 07, 06, 11, 30), false, "address2", "desc2", RepeatingOptions.DAILY, false, null, null) );
		events.add( new Event(0, 3, "BillsEvent", LocalDateTime.of(2022, 7, 8, 11, 00), null, true, "address3", "desc3", RepeatingOptions.WEEKLY, false, null, null) );
		
		events.forEach(event ->{
			System.out.println(event);
			try {
				eventService.addEvent(event);
			} catch (DaoException e) {
				System.out.println(e);
			}
		});
		
		System.out.println("events seeded");
	}
	
	public void seedNotificationsTable() {
	System.out.println("seeding notifications");
		
		List<Notification> notifications = new ArrayList<>();
		notifications.add( new Notification(1, "JohnsNotification", "desc1", LocalDateTime.of(2022, 7, 6, 10, 00), ReminderUnit.MINUTES, 30) );
		notifications.add( new Notification(2, "MarysNotification", "desc2", LocalDateTime.of(2022, 7, 7, 11, 00), ReminderUnit.MINUTES, 30) );
		notifications.add( new Notification(3, "BillsNotification", "desc3", LocalDateTime.of(2022, 7, 6, 10, 00), ReminderUnit.HOURS, 1) );

		
		notifications.forEach(notification ->{
			notification.refresh();
			System.out.println(notification);
			try {
				notificationService.addNotification(notification);
			} catch (DaoException e) {
				System.out.println(e);
			}
		});
		
		System.out.println("notifications seeded");
	}


}
