package ajbc.doodle.calendar.daos;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import ajbc.doodle.calendar.entities.Category;
import ajbc.doodle.calendar.entities.User;

//@SuppressWarnings("unchecked")
@Repository("HtUserDao")
public class HtUserDao implements UserDao {
	
	@Autowired
	private HibernateTemplate template;

	@Override
	public void addUser(User user) throws DaoException {
		template.persist(user);
	}
	
	

}
