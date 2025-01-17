package ajbc.doodle.calendar.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.services.EventService;
import ajbc.doodle.calendar.services.UserService;

@RestController
@RequestMapping("/events")
public class EventController {
	
	@Autowired
	private EventService eventService;
	
	@GetMapping
	public ResponseEntity<List<Event>> getAllEvents() throws DaoException{
		List<Event> list = eventService.getAllEvents();
		
		if (list == null)
			return ResponseEntity.notFound().build();
		
		return ResponseEntity.ok(list);
	}
	
	@GetMapping(path = "/{id}")
	public ResponseEntity<?> getUserById(@PathVariable int id) {
		
		try {
			Event event = eventService.getEventById(id);
			return ResponseEntity.ok(event);
		}
		catch (DaoException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("No event found" , e.getMessage()));	
		}
	}
		
	@PostMapping
	public ResponseEntity<?> addEvent(@RequestBody Event event) {
		try {
			eventService.addEvent(event);
			
			return ResponseEntity.status(HttpStatus.CREATED).body(event);
			
		} catch (DaoException e) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorMessage("Failed to add user to DB", e.getMessage()));
		}
	}
	
	@PutMapping(path = "/{id}")
	public ResponseEntity<?> updateEvent(@RequestBody Event event, @PathVariable Integer id) {

		try {
			event.setEventId(id);
			eventService.updateEvent(event);
			event = eventService.getEventById(event.getEventId());
			return ResponseEntity.status(HttpStatus.OK).body(event);
		} catch (DaoException e) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorMessage("Failed to update event with id: " + id, e.getMessage()));
		}
	} 
	
	@DeleteMapping(path = "{id}")
	public ResponseEntity<?> deleteEvent(@PathVariable Integer id) {

		try {
			eventService.deleteEvent(id);
			return ResponseEntity.status(HttpStatus.OK).body("Event deleted");
		} catch (DaoException e) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorMessage("Failed to delete user with id: " + id, e.getMessage()));
		}
	}
	
}
