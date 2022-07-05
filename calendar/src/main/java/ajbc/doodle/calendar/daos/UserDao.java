package ajbc.doodle.calendar.daos;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.entities.Product;
import ajbc.doodle.calendar.entities.User;



@Transactional(rollbackFor = {DaoException.class}, readOnly = true)
public interface UserDao {

	//CRUD operations
	@Transactional(readOnly = false)
	public default void addUser(User user) throws DaoException {
		throw new DaoException("Method not implemented");
	}
//	@Transactional(readOnly = false)
//	public default void updateProduct(Product product) throws DaoException {
//		throw new DaoException("Method not implemented");
//	}
//
//	public default Product getProduct(Integer productId) throws DaoException {
//		throw new DaoException("Method not implemented");
//	}
//	@Transactional(readOnly = false)
//	public default void deleteProduct(Integer productId) throws DaoException {
//		throw new DaoException("Method not implemented");
//	}

	//Queries
	
	
}
