package ajbc.doodle.calendar.daos;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.entities.Event;

@Transactional(rollbackFor = {DaoException.class}, readOnly = true)
public interface EventDao {

	//CRUD operations
	@Transactional(readOnly = false)
	public void addEvent(Event event) throws DaoException;

	@Transactional(readOnly = false)
	public void updateEvent(Event event) throws DaoException;

	public Event getEventById(int eventId) throws DaoException;

	@Transactional(readOnly = false)
	void deleteEvent(int eventId) throws DaoException;

	List<Event> getAllEvents() throws DaoException;
	
}
