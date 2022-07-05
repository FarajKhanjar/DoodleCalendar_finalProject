package ajbc.doodle.calendar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.Address;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.User;
import ajbc.doodle.calendar.services.AddressService;
import ajbc.doodle.calendar.services.UserService;

@RequestMapping("/addresses")
@RestController
public class AddressController {
	//TODO
//	@Autowired
//	AddressService service;
//	
//	@RequestMapping(method = RequestMethod.GET, path="/{addressId}")
//	public ResponseEntity<?> getAddressById(@PathVariable Integer addressId) {
//		
//		Address address;
//		try { 
//			address = service.getAddress(addressId);
//			return ResponseEntity.ok(address);
//			
//		} catch (DaoException e) {
//			ErrorMessage errorMessage = new ErrorMessage();
//			errorMessage.setData(e.getMessage());
//			errorMessage.setMessage("Failed to get address with id: "+addressId);
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
//		}
//	}
//	
//	@RequestMapping(method = RequestMethod.POST)
//	public ResponseEntity<?> addAddress(@RequestBody Address address) {
//		
//		try {
//			service.addAddress(address);
//			address = service.getAddress(address.getAddressId());
//			return ResponseEntity.status(HttpStatus.CREATED).body(address);
//			
//		} catch (DaoException e) {
//			ErrorMessage errorMessage = new ErrorMessage();
//			errorMessage.setData(e.getMessage());
//			errorMessage.setMessage("Failed to add address to 'addresses' DB");
//			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMessage);
//		}
//	}
//	
//	@RequestMapping(method = RequestMethod.GET)
//	public ResponseEntity<List<Address>> getAllAddresses() throws DaoException{
//		List<Address> list = service.getAllAddresses();
//		if(list==null)
//			return ResponseEntity.notFound().build();
//		return ResponseEntity.ok(list);
//	}
	
}