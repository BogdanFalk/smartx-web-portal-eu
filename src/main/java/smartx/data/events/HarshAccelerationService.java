package smartx.data.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import smartx.ui.forms.TableFormFuelHarsh;
import smartx.ui.forms.TableFormMostRecentEvents;
import smartx.utils.Convertor;


@Service
public class HarshAccelerationService {

	@Autowired 
	private HarshAccelerationRepository harshAccelerationRepository;
	
	public HarshAcceleration addHarshAcceleration(HarshAcceleration harshAcceleration) {
        return harshAccelerationRepository.save(harshAcceleration);
    }

    public HarshAcceleration updateHarshAcceleration(HarshAcceleration harshAcceleration) {
        return harshAccelerationRepository.save(harshAcceleration);
    }
    
    public Boolean deleteHarshAcceleration(int id) {
    	Boolean deletedSuccessfully = false;
    	if (harshAccelerationRepository.existsById(id)) {
    		harshAccelerationRepository.deleteById(id);
            deletedSuccessfully = true;
        }
        return deletedSuccessfully;
    }
    
    public void deleteAllHarshAccelerationsForVehicle(int id) {
    	List<HarshAcceleration> allHarshAccelerations = getAllHarshAccelerationsUnorderedByVehicleId(id);
    	for (HarshAcceleration harshAcceleration : allHarshAccelerations) {
    		harshAccelerationRepository.deleteById(harshAcceleration.getId());
		}
    }

    public HarshAcceleration getHarshAccelerationById(int id) {
        return harshAccelerationRepository.findById(id).get();
    }
    
    public List<HarshAcceleration> getAllHarshAccelerationsUnorderedByVehicleId(int vehicleId) {
		return harshAccelerationRepository.findByVehicleId(vehicleId);
	}
	
	public List<HarshAcceleration> getAllHarshAccelerationsOrderedDescendingByVehicleId(int vehicleId) {
		return harshAccelerationRepository.findByVehicleIdOrderByTimestampDesc(vehicleId);
	}
	
	public HarshAcceleration getMostRecentHarshAccelerationByVehicleId(int vehicleId) {
		return harshAccelerationRepository.findFirstByVehicleIdOrderByTimestampDesc(vehicleId);
	}
    
    public List<HarshAcceleration> getAllHarshAccelerations() {
    	List<HarshAcceleration> harshAccelerations = new ArrayList<>();
    	harshAccelerationRepository.findAll().forEach(harshAccelerations::add);
		return harshAccelerations;
    }
    
    public TableFormMostRecentEvents getMostRecentHarshAccelerationEventDetailsTableForVehicle(int vehicleId, String vehicleNumber) {

		// if no event is found, we should return null
		TableFormMostRecentEvents latestEventTableForm = null;
		
		// retrieve the most recent harsh acceleration event for this vehicle
		HarshAcceleration mostRecentHarshAccelerationEvent = getMostRecentHarshAccelerationByVehicleId(vehicleId);

		if(mostRecentHarshAccelerationEvent != null)
		{
			// create TableFormMostRecentEvents containing information from the most recent harsh acceleration event
			latestEventTableForm = new TableFormMostRecentEvents();
			latestEventTableForm.setTimestamp(mostRecentHarshAccelerationEvent.getTimestamp());
			latestEventTableForm.setLocalDateTime(Convertor.convertTimestamp(mostRecentHarshAccelerationEvent.getTimestamp()));
			latestEventTableForm.setLatitude(Convertor.convertCoordinates(mostRecentHarshAccelerationEvent.getRawLatitude()));
			latestEventTableForm.setLongitude(Convertor.convertCoordinates(mostRecentHarshAccelerationEvent.getRawLongitude()));
			latestEventTableForm.setVehicleNumber(vehicleNumber);
			latestEventTableForm.setTypeName(EventType.HARSH_ACCELERATION.getName());
			latestEventTableForm.setTypeValue(EventType.HARSH_ACCELERATION.getValue());
		}

		return latestEventTableForm;
	}
    
    public List<TableFormFuelHarsh> getHarshAccelerationTableForms(final List<HarshAcceleration> harshAccelerations) {

		// initialize events table list
		List<TableFormFuelHarsh> listOfHarshAccelerationsTableForms = new ArrayList<TableFormFuelHarsh>();
		TableFormFuelHarsh currentTableFormHarshAcceleration = null;

		// for each harsh acceleration event, create a TableFormFuelHarsh object, containing all the necessary information
		// for FE, converted so that it is displayed properly (converted latitude, converted longitude, converted timestamp)
		for (HarshAcceleration harshAccelerationEvent : harshAccelerations)
		{
			// create TableFormFuelHarsh containing information from the harsh acceleration event
			currentTableFormHarshAcceleration = new TableFormFuelHarsh();
			currentTableFormHarshAcceleration.setConvertedTimestamp(Convertor.convertTimestamp(harshAccelerationEvent.getTimestamp()));
			currentTableFormHarshAcceleration.setConvertedLatitude(Convertor.convertCoordinates(harshAccelerationEvent.getRawLatitude()));
			currentTableFormHarshAcceleration.setConvertedLongitude(Convertor.convertCoordinates(harshAccelerationEvent.getRawLongitude()));
			currentTableFormHarshAcceleration.setValue(harshAccelerationEvent.getValue());

			listOfHarshAccelerationsTableForms.add(currentTableFormHarshAcceleration);
		}

		return listOfHarshAccelerationsTableForms;
	}
    
    public void performEventsCleanup(long olderThanUnixTimestamp)
	{
		// get all harsh acceleration events from db, for all the vehicles
		List<HarshAcceleration> harshAccelerationEventsList = getAllHarshAccelerations();

		// sort the list ascending
		Collections.sort(harshAccelerationEventsList, Comparator.comparingLong(HarshAcceleration::getTimestamp));

		HarshAcceleration currentHarshAccelerationEvent;

		for (int i = 0; i < harshAccelerationEventsList.size(); i++)
		{
			currentHarshAccelerationEvent = harshAccelerationEventsList.get(i);
			if(currentHarshAccelerationEvent.getTimestamp() <= olderThanUnixTimestamp)
			{
				System.out.println("Deleting harsh acceleration event with id " + currentHarshAccelerationEvent.getId());
				deleteHarshAcceleration(currentHarshAccelerationEvent.getId());
			}
			else
			{
				System.out.println("Reached event newer than limit, no more harsh acceleration events to delete!");
				break;
			}
		}
	}
	
}
