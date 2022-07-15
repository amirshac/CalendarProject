package ajbc.doodle.calendar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.CalendarException;
import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.entities.UserLoginInfo;
import ajbc.doodle.calendar.entities.webpush.PushMessage;


@Service
public class UserService {

	@Autowired
	@Qualifier("HtUserDao")
	UserDao userDao;
	
	@Autowired
	PushService pushService;
	
	public void addUser(User user) throws DaoException {
		userDao.addUser(user);
	}
	
	public List<User> getAllUsers() throws DaoException{
		return userDao.getAllUsers();
	}
	
	public User getUserById(int id) throws DaoException {
		return userDao.getUserById(id);
	}
	
	public User getUserByEmail(String email) throws DaoException{
		return userDao.getUserByEmail(email);
	}
	
	public void updateUser(User user) throws DaoException{
		userDao.updateUser(user);
	}
	
	public void deleteUser(int userId) throws DaoException{
		userDao.deleteUser(userId);
	}
	
	public void hardDeleteAllUsers() throws DaoException {
		userDao.hardDeleteAllUsers();
	}
	

	public void attemptLogIn(String email, UserLoginInfo info) throws DaoException, CalendarException {
		System.out.println("attemptlogin email: " + email);
		
		User user = userDao.getUserByEmail(email);
		
		info.setUserId(user.getUserId());
		//info.setLoginId(user.getUserId());
		
		user.setLoginInfo(info);
		user.setLoggedIn(true);
		
		userDao.updateUser(user);
		
		// send logged in notification to user
		pushService.sendPushMessageToUser(info, new PushMessage("logged in", "Server push message: you are now logged in"));
		
	}
	
	public void attemptLogout(String email, String endpoint) throws DaoException, CalendarException {
		System.out.println("attemptlogout email: " + email);
		
		User user = userDao.getUserByEmail(email);
		
//		// send logged in notification to user
//		UserLoginInfo info = user.getLoginInfo();
//		pushService.sendPushMessageToUser(info, new PushMessage("logged out", "you are now logged out"));
				
		user.setLoginInfo(null);
		user.setLoggedIn(false);
		
		userDao.updateUser(user);
	}
	

	// QUERIES
	public boolean doesEmailExist(String email) {
		try {
			getUserByEmail(email);
			return true;
		}catch (DaoException e) {
			return false;
		}
	}
	
	public void addEventToUser(int userId, Event event) throws DaoException {
		User user = this.getUserById(userId);
		user.addEvent(event);
		this.updateUser(user);
	}	
}
