package ajbc.doodle.calendar.utils;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.services.UserService;

@Component
public class SeedDB {
	
	@Autowired
	private UserService userService;
	
	@EventListener
	public void seedDB(ContextRefreshedEvent event) {
		try {
			seedUsersTable();
		}catch (DaoException e) {
			System.out.println(e);
		}
	}
	
	public void seedUsersTable() throws DaoException {
		System.out.println("seeding users");

		User user;
		
		user = new User(0, "John", "Doe", "john.doe@gmail.com", LocalDate.of(2022, 01, 01), LocalDate.of(2022, 01, 01), false);
		System.out.println(user);
		
		userService.addUser(user);
		
		System.out.println("users seeded");
	}

}
