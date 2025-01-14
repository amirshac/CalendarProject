package ajbc.doodle.calendar.daos;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.entities.Notification;


@Transactional(rollbackFor = {DaoException.class}, readOnly = true)
public interface NotificationDao {

	@Transactional(readOnly = false)
	public void addNotification(Notification notification) throws DaoException;

	@Transactional(readOnly = false)
	public void updateNotification(Notification notification) throws DaoException;

	@Transactional
	public Notification getNotificationById(int notificationId) throws DaoException;

	@Transactional
	List<Notification> getAllNotifications() throws DaoException;

	@Transactional(readOnly = false)
	void deleteNotification(int notificationId) throws DaoException;	
}
