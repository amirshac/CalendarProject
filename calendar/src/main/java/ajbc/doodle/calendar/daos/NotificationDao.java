package ajbc.doodle.calendar.daos;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;



@Transactional(rollbackFor = {DaoException.class}, readOnly = true)
public interface NotificationDao {

	//CRUD operations
	@Transactional(readOnly = false)
	public void addNotification(Notification notification) throws DaoException;

	@Transactional(readOnly = false)
	public void updateNotification(Notification notification) throws DaoException;

	public Notification getNotification(int notificationId) throws DaoException;

	@Transactional(readOnly = false)
	void deleteNotification(int notificationId) throws DaoException;

	
//	//@Transactional(readOnly = false)
//	public default List<User> getAllUsers() throws DaoException{
//		throw new DaoException("Method not implemented");
//	}
//	
//	@Transactional(readOnly = false)
//	public default void hardDeleteAllUsers() throws DataAccessException, DaoException {
//		throw new DaoException("Method not implemented");
//	}

	//Queries
	
	
}
