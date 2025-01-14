package ajbc.doodle.calendar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.NotificationDao;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;


@Service
public class NotificationService {

	@Autowired
	@Qualifier("HtNotificationDao")
	NotificationDao notificationDao;
	
	@Autowired
	EventService eventService;
	
	public void addNotification(Notification notification) throws DaoException {
		// make sure proper alert time is calculated with refresh
		notification.refresh(); 
		eventService.addNotificationToEvent(notification.getEventId(), notification);
	}
	
	public List<Notification> getAllNotifications() throws DaoException{
		return notificationDao.getAllNotifications();
	}
	
	public Notification getNotificationById(int id) throws DaoException{
		return notificationDao.getNotificationById(id);
	}
	
	public void updateNotification(Notification notification) throws DaoException{
		// make sure proper alert time is calculated with refresh
		notification.refresh(); 
		notificationDao.updateNotification(notification);
	}
	
	// Doesn't re-calculate alert time. Used to postpone alerts in DB
	public void updateNotificationWithoutRefresh(Notification notification) throws DaoException{
		notificationDao.updateNotification(notification);
	}
	
	public void deleteNotification(Integer id) throws DaoException {
		notificationDao.deleteNotification(id);
	}	
	
}
