package ajbc.doodle.calendar.daos;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.entities.User;

@SuppressWarnings("unchecked")
@Repository("HtUserDao")
public class HtUserDao implements UserDao {
	
	@Autowired
	private HibernateTemplate template;

	@Override
	public void addUser(User user) throws DaoException {
		template.persist(user);
	}
	
	@Override
	public void updateUser(User user) throws DaoException {
		template.merge(user);
	}
	
	@Override
	public User getUserById(Integer userId) throws DaoException {
		User user = template.get(User.class, userId);
		if (user == null)
			throw new DaoException("No user in DB with ID: " + userId);
		return user;
	}
	
	@Override
	public User getUserByEmail(String email) throws DaoException{
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
		Criterion criterion = Restrictions.eq("email", email);
		criteria.add(criterion);
		List<User> users = (List<User>)template.findByCriteria(criteria);
		
		if (users == null || users.isEmpty())
			throw new DaoException("No user in DB with email: " + email);
		
		return users.get(0);
	}
	
	@Override
	public void deleteUser(Integer userId) throws DaoException{
		User user = getUserById(userId);
		user.setDeleted(true);
		updateUser(user);
	}
	
	@Override
	public List<User> getAllUsers() throws DaoException{
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
		return (List<User>) template.findByCriteria(criteria);
	}
	
	@Override
	public void hardDeleteAllUsers() throws DataAccessException, DaoException {
		template.deleteAll(getAllUsers());
	}
	

}
