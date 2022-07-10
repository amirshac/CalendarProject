package ajbc.doodle.calendar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.User;


@Service
public class UserService {

	@Autowired
	@Qualifier("HtUserDao")
	UserDao userDao;
	
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
	
	public void hardDeleteAllUsers() throws DaoException {
		userDao.hardDeleteAllUsers();
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

}
