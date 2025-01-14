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
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping
	public ResponseEntity<List<User>> getAllUsers() throws DaoException{
		List<User> list = userService.getAllUsers();
		
		if (list == null)
			return ResponseEntity.notFound().build();
		
		return ResponseEntity.ok(list);
	}
	
	@GetMapping(path = "/{id}")
	public ResponseEntity<?> getUserById(@PathVariable int id) {
		
		try {
			User user = userService.getUserById(id);
			return ResponseEntity.ok(user);
		}
		catch (DaoException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("No user found" , e.getMessage()));	
		}
	}
	
	@GetMapping(path = "/email/{email}")
	public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
		
		try {
			User user = userService.getUserByEmail(email);
			return ResponseEntity.ok(user);
		}
		catch (DaoException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("No user found" , e.getMessage()));	
		}
	} 
	
	@PostMapping
	public ResponseEntity<?> addUser(@RequestBody User user) {
		try {
			userService.addUser(user);
			System.out.println("user added to DB " + user);
			return ResponseEntity.status(HttpStatus.CREATED).body(user);
			
		} catch (DaoException e) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorMessage("Failed to add user to DB", e.getMessage()));
		}
	}
	
	@PutMapping(path = "/{id}")
	public ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable Integer id) {

		try {
			user.setUserId(id);
			userService.updateUser(user);
			user = userService.getUserById(user.getUserId());
			return ResponseEntity.status(HttpStatus.OK).body(user);
		} catch (DaoException e) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorMessage("Failed to update user with id: " + id, e.getMessage()));
		}
	} 
	
	@DeleteMapping(path = "{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Integer id) {

		try {
			userService.deleteUser(id);
			return ResponseEntity.status(HttpStatus.OK).body("User deleted");
		} catch (DaoException e) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorMessage("Failed to delete user with id: " + id, e.getMessage()));
		}
	} 
	
	
}
