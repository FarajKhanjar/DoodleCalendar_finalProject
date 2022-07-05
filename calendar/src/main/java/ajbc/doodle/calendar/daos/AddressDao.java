package ajbc.doodle.calendar.daos;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.entities.Address;

@Transactional(rollbackFor = {DaoException.class}, readOnly = true)
public interface AddressDao {
	//TODO
//	//CRUD 
//	public default Address getAddress(Integer addressId) throws DaoException {
//		throw new DaoException("Method not implemented");
//	}
//
//	@Transactional(readOnly = false)
//	public default void addAddress(Address address) throws DaoException {
//		throw new DaoException("Method not implemented");
//	}
//
////	@Transactional(readOnly = false)
////	public default void updateUser(User user) throws DaoException {
////		throw new DaoException("Method not implemented");
////	}
////
////	@Transactional(readOnly = false)
////	public default void deleteUser(Integer userId) throws DaoException {
////		throw new DaoException("Method not implemented");
////	}
//	
//	
//	
//	
//	
//	// QUERIES
//		public default List<Address> getAllAddresses() throws DaoException {
//			throw new DaoException("Method not implemented");
//		}
//
//
//		public default long countAddresses() throws DaoException {
//			throw new DaoException("Method not implemented");
//		}
//
////		@Transactional(readOnly = false)
////		public default void deleteAllUsers() throws DaoException {
////			throw new DaoException("Method not implemented");
////		}
////		
////		public default List<User> getDiscontinuedUsers() throws DaoException {
////			throw new DaoException("Method not implemented");
////		}
}