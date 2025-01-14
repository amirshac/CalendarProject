package ajbc.doodle.calendar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.EventDao;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;


@Service
public class EventService {

	@Autowired
	@Qualifier("HtEventDao")
	EventDao eventDao;
	
	@Autowired
	UserService userService;
	
	public void addEvent(Event event) throws DaoException {
		eventDao.addEvent(event);
		userService.addEventToUser(event.getOwnerId(), event);
	}
	
	public List<Event> getAllEvents() throws DaoException{
		return eventDao.getAllEvents();
	}
	
	public Event getEventById(int id) throws DaoException{
		return eventDao.getEventById(id);
	}
	
	public void updateEvent(Event event) throws DaoException{
		eventDao.updateEvent(event);
	}
	
	public void deleteEvent(int id) throws DaoException{
		eventDao.deleteEvent(id);
	}
	
	public void addNotificationToEvent(int eventId, Notification notification) throws DaoException {
		Event event = this.getEventById(eventId);
		event.addNotification(notification);
		this.updateEvent(event);
	}
	
}
