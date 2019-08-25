package smartx.data.driver;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import smartx.data.vehicle.Vehicle;

@Service
public class DriverService {

	@Autowired
	private DriverRepository driverRepository;

	public Driver addDriver(Driver driver) {
		return driverRepository.save(driver);
	}

	public Driver updateDriver(Driver driver) {
		return driverRepository.save(driver);
	}

	public Boolean deleteDriver(int id) {
		Boolean deletedSuccessfully = false;
		if (driverRepository.existsById(id)) 
		{
			// delete the assigned vehicle to guard against relational issues
			Driver driverToBeDeleted = getDriverById(id);
			Vehicle assignedVehicle = driverToBeDeleted.getVehicle();
			if(assignedVehicle != null)
			{
				driverToBeDeleted.setVehicle(null);

				// we also must keep the vehicle records up-to-date, so we need to delete the driver from this vehicle
				assignedVehicle.setDriver(null);
			}

			// delete driver
			driverRepository.deleteById(id);
			deletedSuccessfully = true;
		}
		return deletedSuccessfully;
	}

	public Driver getDriverById(int id) {
		return driverRepository.findById(id).get();
	}

	public Driver getDriverByVehicleId(int vehicleId) {
		return driverRepository.findByVehicleId(vehicleId);
	}

	public List<Driver> getDriversByUserId(int userId) {
		return driverRepository.findByUserId(userId);
	}

	public List<String> getDriversUsernamesByUserId(int userId) {
		List<String> driversUsernames = new ArrayList<>();
		List<Driver> allUsersDriversList = getDriversByUserId(userId);
		for(Driver driver : allUsersDriversList)
		{
			driversUsernames.add(driver.getDriverUsername());
		}
		return driversUsernames;
	}

	public List<Driver> getUnassignedDriversByUserId(int userId) {
		List<Driver> unassignedDrivers = new ArrayList<>();
		List<Driver> allUsersDriversList = getDriversByUserId(userId);
		for(Driver driver : allUsersDriversList)
		{
			if(driver.getVehicle() == null)
				unassignedDrivers.add(driver);
		}
		return unassignedDrivers;
	}

	public List<Driver> getDriversByUserUsername(String userUsername) {
		return driverRepository.findByUserUsername(userUsername);
	}

	public List<Driver> getAllDrivers() {
		List<Driver> drivers = new ArrayList<>();
		driverRepository.findAll().forEach(drivers::add);
		return drivers;
	}

}
