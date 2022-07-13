package ajbc.doodle.calendar.daos;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.entities.Event;

@SuppressWarnings("unchecked")
@Repository("HtEventDao")
public class HtEventDao implements EventDao {
	
	@Autowired
	private HibernateTemplate template;

	@Override
	public void addEvent(Event event) throws DaoException {
		template.persist(event);
	}
	
	@Override
	public void updateEvent(Event event) throws DaoException {
		template.merge(event);
	}
		
	@Override
	public Event getEventById(int eventId) throws DaoException {
		Event event = template.get(Event.class, eventId);
		if (event == null)
			throw new DaoException("No event in DB with ID: " + eventId);
		return event;
	}
	
	@Override
	public void deleteEvent(int eventId) throws DaoException{
		Event event = getEventById(eventId);
		event.setDeleted(true);
		updateEvent(event);
	}
	
	@Override
	public List<Event> getAllEvents() throws DaoException{
		DetachedCriteria criteria = DetachedCriteria.forClass(Event.class);
		return (List<Event>) template.findByCriteria(criteria);
	}
	
	

}
