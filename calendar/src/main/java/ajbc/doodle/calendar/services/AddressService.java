package ajbc.doodle.calendar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.AddressDao;
import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.Address;

@Component
public class AddressService {
	
	@Autowired
	private AddressDao addressDao;

	// CRUD
	public List<Address> getAllAddresses() throws DaoException {
		return addressDao.getAllAddresses();
	}
	
	public void addAddress(Address address) throws DaoException {
		addressDao.addAddress(address);
	}
	
	public void updateAddress(Address address) throws DaoException {
		addressDao.updateAddress(address);
	}

	// Queries
	public Address getAddressById(Integer addressId) throws DaoException {
		return addressDao.getAddressById(addressId);
	}

	public List<Address> getAddressByCountry(String country) throws DaoException {
		return addressDao.getAddressByCountry(country);
	}
	
	public List<Address> getAddressByCity(String city) throws DaoException {
		return addressDao.getAddressByCity(city);
	}
	
}