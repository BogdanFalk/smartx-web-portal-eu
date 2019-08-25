package smartx.data.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smartx.ui.forms.TableFormMostRecentEvents;
import smartx.ui.forms.TableFormOver;
import smartx.utils.Convertor;


@Service
public class OverRpmService {

	@Autowired 
	private OverRpmRepository overRpmRepository;
	
	public OverRpm addOverRpm(OverRpm overRpm) {
        return overRpmRepository.save(overRpm);
    }

    public OverRpm updateOverRpm(OverRpm overRpm) {
        return overRpmRepository.save(overRpm);
    }
    
    public Boolean deleteOverRpm(int id) {
    	Boolean deletedSuccessfully = false;
    	if (overRpmRepository.existsById(id)) {
    		overRpmRepository.deleteById(id);
            deletedSuccessfully = true;
        }
        return deletedSuccessfully;
    }
    
    public void deleteAllOverRpmsForVehicle(int id) {
    	List<OverRpm> allOverRpms = getAllOverRpmsUnorderedByVehicleId(id);
    	for (OverRpm overRpm : allOverRpms) {
    		overRpmRepository.deleteById(overRpm.getId());
		}
    }

    public OverRpm getOverRpmById(int id) {
        return overRpmRepository.findById(id).get();
    }
    
    public List<OverRpm> getAllOverRpmsUnorderedByVehicleId(int vehicleId) {
		return overRpmRepository.findByVehicleId(vehicleId);
	}
	
	public List<OverRpm> getAllOverRpmsOrderedDescendingByVehicleId(int vehicleId) {
		return overRpmRepository.findByVehicleIdOrderByTimestampDesc(vehicleId);
	}
	
	public OverRpm getMostRecentOverRpmByVehicleId(int vehicleId) {
		return overRpmRepository.findFirstByVehicleIdOrderByTimestampDesc(vehicleId);
	}
    
    public List<OverRpm> getAllOverRpms() {
    	List<OverRpm> overRpms = new ArrayList<>();
    	overRpmRepository.findAll().forEach(overRpms::add);
		return overRpms;
    }
    
    public TableFormMostRecentEvents getMostRecentOverRpmEventDetailsTableForVehicle(int vehicleId, String vehicleNumber) {

		// if no event is found, we should return null
		TableFormMostRecentEvents latestEventTableForm = null;
		
		// retrieve the most recent over rpm event for this vehicle
		OverRpm mostRecentOverRpmEvent = getMostRecentOverRpmByVehicleId(vehicleId);

		if(mostRecentOverRpmEvent != null)
		{
			// create TableFormMostRecentEvents containing information from the most recent over rpm event
			latestEventTableForm = new TableFormMostRecentEvents();
			latestEventTableForm.setTimestamp(mostRecentOverRpmEvent.getTimestamp());
			latestEventTableForm.setLocalDateTime(Convertor.convertTimestamp(mostRecentOverRpmEvent.getTimestamp()));
			latestEventTableForm.setLatitude(Convertor.convertCoordinates(mostRecentOverRpmEvent.getRawLatitude()));
			latestEventTableForm.setLongitude(Convertor.convertCoordinates(mostRecentOverRpmEvent.getRawLongitude()));
			latestEventTableForm.setVehicleNumber(vehicleNumber);
			latestEventTableForm.setTypeName(EventType.OVER_RPM.getName());
			latestEventTableForm.setTypeValue(EventType.OVER_RPM.getValue());
		}

		return latestEventTableForm;
	}
    
    public List<TableFormOver> getOverRpmTableForms(final List<OverRpm> overRpms) {

		// initialize events table list
		List<TableFormOver> listOfOverRpmsTableForms = new ArrayList<TableFormOver>();
		TableFormOver currentTableFormOverRpm = null;

		// for each over rpm event, create a TableFormOver object, containing all the necessary information
		// for FE, converted so that it is displayed properly (converted latitude, converted longitude, converted timestamp)
		for (OverRpm overRpmEvent : overRpms)
		{
			// create TableFormOver containing information from the over rpm event
			currentTableFormOverRpm = new TableFormOver();
			currentTableFormOverRpm.setConvertedTimestamp(Convertor.convertTimestamp(overRpmEvent.getTimestamp()));
			currentTableFormOverRpm.setConvertedLatitude(Convertor.convertCoordinates(overRpmEvent.getRawLatitude()));
			currentTableFormOverRpm.setConvertedLongitude(Convertor.convertCoordinates(overRpmEvent.getRawLongitude()));
			currentTableFormOverRpm.setValue(overRpmEvent.getValue());
			currentTableFormOverRpm.setDuration(overRpmEvent.getDuration());

			listOfOverRpmsTableForms.add(currentTableFormOverRpm);
		}

		return listOfOverRpmsTableForms;
	}
    
    public void performEventsCleanup(long olderThanUnixTimestamp)
	{
		// get all over rpm events from db, for all the vehicles
		List<OverRpm> overRpmEventsList = getAllOverRpms();

		// sort the list ascending
		Collections.sort(overRpmEventsList, Comparator.comparingLong(OverRpm::getTimestamp));

		OverRpm currentOverRpmEvent;

		for (int i = 0; i < overRpmEventsList.size(); i++)
		{
			currentOverRpmEvent = overRpmEventsList.get(i);
			if(currentOverRpmEvent.getTimestamp() <= olderThanUnixTimestamp)
			{
				System.out.println("Deleting over rpm event with id " + currentOverRpmEvent.getId());
				deleteOverRpm(currentOverRpmEvent.getId());
			}
			else
			{
				System.out.println("Reached event newer than limit, no more over rpm events to delete!");
				break;
			}
		}
	}
	
}
