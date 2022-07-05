package ajbc.doodle.calendar.hibernates;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.AddressDao;
import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.Address;

@Component
@SuppressWarnings("unchecked")
public class HibernateTemplateAddressDao implements AddressDao {

	//TODO
//	@Autowired
//	private HibernateTemplate template;
//	
//	@Override
//	public Address getAddress(Integer addressId) throws DaoException {
//		Address address = template.get(Address.class, addressId);
//		if (address ==null)
//			throw new DaoException("No Such Address in DB");
//		return address;
//	}
//	
//	@Override
//	public void addAddress(Address address) throws DaoException {
//		template.persist(address);
//	}
//	
////	@Override
////	public void updateUser(User user) throws DaoException {
////		template.merge(user);
////	}
//	
////	@Override
////	public void deleteUser(Integer userId) throws DaoException {	
////		User product = getUser(userId);
////		product.setDiscontinued(1);
////		updateUser(product);
////	}
//
//
//	@Override
//	public List<Address> getAllAddresses() throws DaoException {
//		DetachedCriteria criteria = DetachedCriteria.forClass(Address.class);
//		return (List<Address>)template.findByCriteria(criteria);
//	}
//	
////	@Override
////	public void deleteAllUsers() throws DataAccessException, DaoException {
////		template.deleteAll(getAllUsers());
////	}

	
	
	
}
