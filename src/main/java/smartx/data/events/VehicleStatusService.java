package smartx.data.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class VehicleStatusService {

	@Autowired
	private VehicleStatusRepository vehicleStatusRepository;
	
	public VehicleStatus addVehicleStatus(VehicleStatus vehicleStatus) {
        return vehicleStatusRepository.save(vehicleStatus);
    }

    public VehicleStatus updateVehicleStatus(VehicleStatus vehicleStatus) {
        return vehicleStatusRepository.save(vehicleStatus);
    }
    
    public Boolean deleteVehicleStatus(int id) {
    	Boolean deletedSuccessfully = false;
    	if (vehicleStatusRepository.existsById(id)) {
    		vehicleStatusRepository.deleteById(id);
            deletedSuccessfully = true;
        }
        return deletedSuccessfully;
    }
    
    public void deleteAllVehicleStatusesForVehicle(int id) {
    	List<VehicleStatus> allVehicleStatuses = getAllVehicleStatusesUnorderedByVehicleId(id);
    	for (VehicleStatus vehicleStatus : allVehicleStatuses) {
    		vehicleStatusRepository.deleteById(vehicleStatus.getId());
		}
    }

    public VehicleStatus getVehicleStatusById(int id) {
        return vehicleStatusRepository.findById(id).get();
    }
    
    public List<VehicleStatus> getAllVehicleStatusesUnorderedByVehicleId(int vehicleId) {
		return vehicleStatusRepository.findByVehicleId(vehicleId);
	}
	
	public List<VehicleStatus> getAllVehicleStatusesOrderedDescendingByVehicleId(int vehicleId) {
		return vehicleStatusRepository.findByVehicleIdOrderByTimestampDesc(vehicleId);
	}
	
	public VehicleStatus getMostRecentVehicleStatusByVehicleId(int vehicleId) {
		return vehicleStatusRepository.findFirstByVehicleIdOrderByTimestampDesc(vehicleId);
	}
    
    public List<VehicleStatus> getAllVehicleStatuses() {
    	List<VehicleStatus> vehicleStatuses = new ArrayList<>();
    	vehicleStatusRepository.findAll().forEach(vehicleStatuses::add);
		return vehicleStatuses;
    }
    
    public void performEventsCleanup(long olderThanUnixTimestamp)
	{
		// get all vehicle status events from db, for all the vehicles
		List<VehicleStatus> vehicleStatusEventsList = getAllVehicleStatuses();

		// sort the list ascending
		Collections.sort(vehicleStatusEventsList, Comparator.comparingLong(VehicleStatus::getTimestamp));

		VehicleStatus currentVehicleStatusEvent;

		for (int i = 0; i < vehicleStatusEventsList.size(); i++)
		{
			currentVehicleStatusEvent = vehicleStatusEventsList.get(i);
			if(currentVehicleStatusEvent.getTimestamp() <= olderThanUnixTimestamp)
			{
				System.out.println("Deleting vehicle status event with id " + currentVehicleStatusEvent.getId());
				deleteVehicleStatus(currentVehicleStatusEvent.getId());
			}
			else
			{
				System.out.println("Reached event newer than limit, no more vehicle status events to delete!");
				break;
			}
		}
	}
	
}
