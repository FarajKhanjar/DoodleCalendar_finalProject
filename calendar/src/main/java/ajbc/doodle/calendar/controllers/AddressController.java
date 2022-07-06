package ajbc.doodle.calendar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.Address;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.services.AddressService;

@RequestMapping("/addresses")
@RestController
public class AddressController {

	@Autowired
	private AddressService addressService;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Address>> getAllAddresses() throws DaoException {
		List<Address> allAddresses = addressService.getAllAddresses();
		if (allAddresses == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		return ResponseEntity.ok(allAddresses);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> AddAddress(@RequestBody Address address) {
		try {
			addressService.addAddress(address);
			address = addressService.getAddressById(address.getAddressId());
			return ResponseEntity.status(HttpStatus.CREATED).body(address);
			
		} catch (DaoException e) {
			ErrorMessage errorMsg = new ErrorMessage();
			errorMsg.setData(e.getMessage());
			errorMsg.setMessage("Failed to add address to DB");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMsg);
		}
	}

	@RequestMapping(method = RequestMethod.PUT, path = "/byId/{id}")
	public ResponseEntity<?> updateAddress(@RequestBody Address address, @PathVariable Integer id) {
		try {
			address.setAddressId(id);
			addressService.updateAddress(address);
			address = addressService.getAddressById(id);
			return ResponseEntity.status(HttpStatus.OK).body(address);
			
		} catch (DaoException e) {
			ErrorMessage errorMsg = new ErrorMessage();
			errorMsg.setData(e.getMessage());
			errorMsg.setMessage("Failed to update this address");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMsg);
		}
	}

	@RequestMapping(method = RequestMethod.GET, path = "/byId/{id}")
	public ResponseEntity<?> getAddressById(@PathVariable Integer id) throws DaoException {
		try {
			Address address = addressService.getAddressById(id);
			return ResponseEntity.ok(address);
			
		} catch (DaoException e) {
			ErrorMessage errorMsg = new ErrorMessage();
			errorMsg.setData(e.getMessage());
			errorMsg.setMessage("Failed to get address By this Id");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMsg);
		}

	}

	@RequestMapping(method = RequestMethod.GET, path = "/byCountry/{country}")
	public ResponseEntity<?> getAddressByCountry(@PathVariable String country) throws DaoException {
		
		List<Address> addressesInCountry;
		try {
			addressesInCountry = addressService.getAddressByCountry(country);
			return ResponseEntity.ok(addressesInCountry);
			
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("Failed to get address By this country");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/byCity/{city}")
	public ResponseEntity<?> getAddressByCity(@PathVariable String city) throws DaoException {
		
		List<Address> addressesInCity;
		try {
			addressesInCity = addressService.getAddressByCity(city);
			return ResponseEntity.ok(addressesInCity);
			
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("Failed to get address By this city");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
		}
	}
	
}