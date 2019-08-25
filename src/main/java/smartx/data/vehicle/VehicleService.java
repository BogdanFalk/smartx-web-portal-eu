package smartx.data.vehicle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import smartx.data.driver.Driver;
import smartx.ui.forms.TableFormMostRecentEvents;
import smartx.utils.Status;

import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleService {

	@Autowired
	private VehicleRepository vehicleRepository;
	@Autowired
	private smartx.data.configuration.DeviceConfigurationService deviceConfigurationService;
	@Autowired
	private smartx.data.configuration.VehicleConfigurationService vehicleConfigurationService;
	@Autowired
	private smartx.data.events.BorderCrossingService borderCrossingService;
	@Autowired
	private smartx.data.events.DtcService dtcService;
	@Autowired
	private smartx.data.events.EngineStatusService engineStatusService;
	@Autowired
	private smartx.data.events.FuelConsumptionService fuelConsumptionService;
	@Autowired
	private smartx.data.events.HarshAccelerationService harshAccelerationService;
	@Autowired
	private smartx.data.events.HarshBreakService harshBreakService;
	@Autowired
	private smartx.data.events.OverRpmService overRpmService;
	@Autowired
	private smartx.data.events.OverSpeedService overSpeedService;
	@Autowired
	private smartx.data.events.TireConditionService tireConditionService;
	@Autowired
	private smartx.data.events.VehicleTrackingService vehicleTrackingService;
	@Autowired
	private smartx.data.events.VehicleStatusService vehicleStatusService;

	public Vehicle addVehicle(Vehicle vehicle) {
		return vehicleRepository.save(vehicle);
	}

	public Vehicle updateVehicle(Vehicle vehicle) {
		return vehicleRepository.save(vehicle);
	}

	public Boolean deleteVehicle(int id) {
		Boolean deletedSuccessfully = false;
		if (vehicleRepository.existsById(id)) {

			// delete all device & vehicle configurations
			deviceConfigurationService.deleteAllDeviceConfigurationsForVehicle(id);
			vehicleConfigurationService.deleteAllVehicleConfigurationsForVehicle(id);

			// delete all the vehicle's events
			borderCrossingService.deleteAllBorderCrossingsForVehicle(id);
			dtcService.deleteAllDtcsForVehicle(id);
			engineStatusService.deleteAllEngineStatusesForVehicle(id);
			fuelConsumptionService.deleteAllFuelConsumptionsForVehicle(id);
			harshAccelerationService.deleteAllHarshAccelerationsForVehicle(id);
			harshBreakService.deleteAllHarshBreaksForVehicle(id);
			overRpmService.deleteAllOverRpmsForVehicle(id);
			overSpeedService.deleteAllOverSpeedsForVehicle(id);
			tireConditionService.deleteAllTireConditionsForVehicle(id);
			vehicleTrackingService.deleteAllVehicleTrackingsForVehicle(id);

			// delete all the vehicle statuses
			vehicleStatusService.deleteAllVehicleStatusesForVehicle(id);

			// delete the assigned driver to guard against relational issues
			Vehicle vehicleToBeDeleted = getVehicleById(id);
			Driver assignedDriver = vehicleToBeDeleted.getDriver();
			if(assignedDriver != null)
			{
				vehicleToBeDeleted.setDriver(null);
				
				// we also must keep the driver records up-to-date, so we need to delete the vehicle from this driver
				assignedDriver.setVehicle(null);
			}
			
			//delete vehicle
			vehicleRepository.deleteById(id);
			deletedSuccessfully = true;
		}
		return deletedSuccessfully;
	}

	public Vehicle getVehicleById(int id) {
		return vehicleRepository.findById(id).get();
	}

	public Vehicle getVehicleBySerialNumber(String deviceSerialNumber) {
		return vehicleRepository.findByDeviceSerialNumber(deviceSerialNumber);
	}

	public List<Vehicle> getVehiclesByStatus(String status) {
		return vehicleRepository.findByStatus(status);
	}

	public List<Vehicle> getVehiclesByUserId(int userId) {
		return vehicleRepository.findByUserId(userId);
	}

	public List<Vehicle> getVehiclesByUserUsername(String userUsername) {
		return vehicleRepository.findByUserUsername(userUsername);
	}

	public List<Vehicle> getVehiclesByUserIdAndStatus(int userId, String status) {
		return  vehicleRepository.findByUserIdAndStatus(userId, status);
	}

	public List<Vehicle> getAllVehicles() {
		List<Vehicle> vehicles = new ArrayList<>();
		vehicleRepository.findAll().forEach(vehicles::add);
		return vehicles;
	}

	public List<Vehicle> getAllVehiclesExceptUnavailable() {
		List<Vehicle> vehicles = new ArrayList<>();
		vehicleRepository.findAll().forEach(vehicle->{
			if(!vehicle.getStatus().equals(Status.UNAVAILABLE.getValue()))
			{
				vehicles.add(vehicle);
			}
		});
		return vehicles;
	}

	public List<TableFormMostRecentEvents> getMostRecentsEventDetailsTablesForVehicle(int vehicleId, String vehicleNumber) {

		List<TableFormMostRecentEvents> allRecentEventsList = new ArrayList<TableFormMostRecentEvents>();

		// add the most recent border crossing event
		TableFormMostRecentEvents borderCrossingEventDetailsTable = borderCrossingService.getMostRecentBorderCrossingEventDetailsTableForVehicle(vehicleId, vehicleNumber);
		if(borderCrossingEventDetailsTable != null)
		{
			allRecentEventsList.add(borderCrossingEventDetailsTable);
		}

		// add the most recent dtc event
		TableFormMostRecentEvents dtcEventDetailsTable = dtcService.getMostRecentDtcEventDetailsTableForVehicle(vehicleId, vehicleNumber);
		if(dtcEventDetailsTable != null)
		{
			allRecentEventsList.add(dtcEventDetailsTable);
		}

		// add the most recent engine status event
		TableFormMostRecentEvents engineStatusEventDetailsTable = engineStatusService.getMostRecentEngineStatusEventDetailsTableForVehicle(vehicleId, vehicleNumber);
		if(engineStatusEventDetailsTable != null)
		{
			allRecentEventsList.add(engineStatusEventDetailsTable);
		}

		// add the most recent fuel consumption event
		TableFormMostRecentEvents fuelConsumptionEventDetailsTable = fuelConsumptionService.getMostRecentFuelConsumptionEventDetailsTableForVehicle(vehicleId, vehicleNumber);
		if(fuelConsumptionEventDetailsTable != null)
		{
			allRecentEventsList.add(fuelConsumptionEventDetailsTable);
		}

		// add the most recent harsh acceleration event
		TableFormMostRecentEvents harshAccelerationEventDetailsTable = harshAccelerationService.getMostRecentHarshAccelerationEventDetailsTableForVehicle(vehicleId, vehicleNumber);
		if(harshAccelerationEventDetailsTable != null)
		{
			allRecentEventsList.add(harshAccelerationEventDetailsTable);
		}

		// add the most recent harsh break event
		TableFormMostRecentEvents harshBreakEventDetailsTable = harshBreakService.getMostRecentHarshBreakEventDetailsTableForVehicle(vehicleId, vehicleNumber);
		if(harshBreakEventDetailsTable != null)
		{
			allRecentEventsList.add(harshBreakEventDetailsTable);
		}

		// add the most recent over rpm event
		TableFormMostRecentEvents overRpmEventDetailsTable = overRpmService.getMostRecentOverRpmEventDetailsTableForVehicle(vehicleId, vehicleNumber);
		if(overRpmEventDetailsTable != null)
		{
			allRecentEventsList.add(overRpmEventDetailsTable);
		}

		// add the most recent over speed event
		TableFormMostRecentEvents overSpeedEventDetailsTable = overSpeedService.getMostRecentOverSpeedEventDetailsTableForVehicle(vehicleId, vehicleNumber);
		if(overSpeedEventDetailsTable != null)
		{
			allRecentEventsList.add(overSpeedEventDetailsTable);
		}

		// add the most recent tire condition event
		TableFormMostRecentEvents tireConditionEventDetailsTable = tireConditionService.getMostRecentTireConditionEventDetailsTableForVehicle(vehicleId, vehicleNumber);
		if(tireConditionEventDetailsTable != null)
		{
			allRecentEventsList.add(tireConditionEventDetailsTable);
		}

		// add the most recent vehicle tracking event
		TableFormMostRecentEvents vehicleTrackingEventDetailsTable = vehicleTrackingService.getMostRecentVehicleTrackingEventDetailsTableForVehicle(vehicleId, vehicleNumber);
		if(vehicleTrackingEventDetailsTable != null)
		{
			allRecentEventsList.add(vehicleTrackingEventDetailsTable);
		}

		return allRecentEventsList;
	}
	
	public List<Vehicle> getUnassignedVehiclesByUserId(int userId) {
		List<Vehicle> unassignedVehicles = new ArrayList<>();
		List<Vehicle> allUsersVehiclesList = getVehiclesByUserId(userId);
		for(Vehicle vehicle : allUsersVehiclesList)
		{
			if(vehicle.getDriver() == null)
				unassignedVehicles.add(vehicle);
		}
		return unassignedVehicles;
	}

}
