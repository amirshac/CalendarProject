package ajbc.doodle.calendar.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.services.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
	
	@Autowired
	private NotificationService notificationService;
	
	@GetMapping
	public ResponseEntity<List<Notification>> getAllNotifications() throws DaoException{
		List<Notification> list = notificationService.getAllNotifications();
		
		if (list == null)
			return ResponseEntity.notFound().build();
		
		return ResponseEntity.ok(list);
	}
	
	@GetMapping(path = "/{id}")
	public ResponseEntity<?> getNotificationById(@PathVariable int id) {
		
		try {
			Notification notification = notificationService.getNotificationById(id);
			return ResponseEntity.ok(notification);
		}
		catch (DaoException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("No notification found" , e.getMessage()));	
		}
	}
		
	@PostMapping
	public ResponseEntity<?> addNotification(@RequestBody Notification notification) {
		try {
			notificationService.addNotification(notification);
			return ResponseEntity.status(HttpStatus.CREATED).body(notification);
			
		} catch (DaoException e) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorMessage("Failed to add notification to DB", e.getMessage()));
		}
	}
	
	@PutMapping(path = "/{id}")
	public ResponseEntity<?> updateNotification(@RequestBody Notification notification, @PathVariable Integer id) {

		try {
			notification.setNotificationId(id);
			notificationService.updateNotification(notification);
			notification = notificationService.getNotificationById(notification.getNotificationId());
			return ResponseEntity.status(HttpStatus.OK).body(notification);
		} catch (DaoException e) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorMessage("Failed to update notification with id: " + id, e.getMessage()));
		}
	} 
	
	@DeleteMapping(path = "{id}")
	public ResponseEntity<?> deleteNotification(@PathVariable Integer id) {

		try {
			notificationService.deleteNotification(id);
			return ResponseEntity.status(HttpStatus.OK).body("Notification deleted");
		} catch (DaoException e) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorMessage("Failed to delete notification with id: " + id, e.getMessage()));
		}
	}
	
}
