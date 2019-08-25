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
public class HarshBreakService {

	@Autowired 
	private HarshBreakRepository harshBreakRepository;
	
	public HarshBreak addHarshBreak(HarshBreak harshBreak) {
        return harshBreakRepository.save(harshBreak);
    }

    public HarshBreak updateHarshBreak(HarshBreak harshBreak) {
        return harshBreakRepository.save(harshBreak);
    }
    
    public Boolean deleteHarshBreak(int id) {
    	Boolean deletedSuccessfully = false;
    	if (harshBreakRepository.existsById(id)) {
    		harshBreakRepository.deleteById(id);
            deletedSuccessfully = true;
        }
        return deletedSuccessfully;
    }
    
    public void deleteAllHarshBreaksForVehicle(int id) {
    	List<HarshBreak> allHarshBreaks = getAllHarshBreaksUnorderedByVehicleId(id);
    	for (HarshBreak harshBreak : allHarshBreaks) {
    		harshBreakRepository.deleteById(harshBreak.getId());
		}
    }

    public HarshBreak getHarshBreakById(int id) {
        return harshBreakRepository.findById(id).get();
    }
    
    public List<HarshBreak> getAllHarshBreaksUnorderedByVehicleId(int vehicleId) {
		return harshBreakRepository.findByVehicleId(vehicleId);
	}
	
	public List<HarshBreak> getAllHarshBreaksOrderedDescendingByVehicleId(int vehicleId) {
		return harshBreakRepository.findByVehicleIdOrderByTimestampDesc(vehicleId);
	}
	
	public HarshBreak getMostRecentHarshBreakByVehicleId(int vehicleId) {
		return harshBreakRepository.findFirstByVehicleIdOrderByTimestampDesc(vehicleId);
	}
    
    public List<HarshBreak> getAllHarshBreaks() {
    	List<HarshBreak> harshBreaks = new ArrayList<>();
    	harshBreakRepository.findAll().forEach(harshBreaks::add);
		return harshBreaks;
    }
    
    public TableFormMostRecentEvents getMostRecentHarshBreakEventDetailsTableForVehicle(int vehicleId, String vehicleNumber) {

		// if no event is found, we should return null
		TableFormMostRecentEvents latestEventTableForm = null;
		
		// retrieve the most recent harsh break event for this vehicle
		HarshBreak mostRecentHarshBreakEvent = getMostRecentHarshBreakByVehicleId(vehicleId);

		if(mostRecentHarshBreakEvent != null)
		{
			// create TableFormMostRecentEvents containing information from the most recent harsh break event
			latestEventTableForm = new TableFormMostRecentEvents();
			latestEventTableForm.setTimestamp(mostRecentHarshBreakEvent.getTimestamp());
			latestEventTableForm.setLocalDateTime(Convertor.convertTimestamp(mostRecentHarshBreakEvent.getTimestamp()));
			latestEventTableForm.setLatitude(Convertor.convertCoordinates(mostRecentHarshBreakEvent.getRawLatitude()));
			latestEventTableForm.setLongitude(Convertor.convertCoordinates(mostRecentHarshBreakEvent.getRawLongitude()));
			latestEventTableForm.setVehicleNumber(vehicleNumber);
			latestEventTableForm.setTypeName(EventType.HARSH_BREAK.getName());
			latestEventTableForm.setTypeValue(EventType.HARSH_BREAK.getValue());
		}

		return latestEventTableForm;
	}
    
    public List<TableFormFuelHarsh> getHarshBreakTableForms(final List<HarshBreak> harshBreaks) {

		// initialize events table list
		List<TableFormFuelHarsh> listOfHarshBreaksTableForms = new ArrayList<TableFormFuelHarsh>();
		TableFormFuelHarsh currentTableFormHarshBreak = null;

		// for each harsh break event, create a TableFormFuelHarsh object, containing all the necessary information
		// for FE, converted so that it is displayed properly (converted latitude, converted longitude, converted timestamp)
		for (HarshBreak harshBreakEvent : harshBreaks)
		{
			// create TableFormFuelHarsh containing information from the harsh break event
			currentTableFormHarshBreak = new TableFormFuelHarsh();
			currentTableFormHarshBreak.setConvertedTimestamp(Convertor.convertTimestamp(harshBreakEvent.getTimestamp()));
			currentTableFormHarshBreak.setConvertedLatitude(Convertor.convertCoordinates(harshBreakEvent.getRawLatitude()));
			currentTableFormHarshBreak.setConvertedLongitude(Convertor.convertCoordinates(harshBreakEvent.getRawLongitude()));
			currentTableFormHarshBreak.setValue(harshBreakEvent.getValue());

			listOfHarshBreaksTableForms.add(currentTableFormHarshBreak);
		}

		return listOfHarshBreaksTableForms;
	}
    
    public void performEventsCleanup(long olderThanUnixTimestamp)
	{
		// get all harsh break events from db, for all the vehicles
		List<HarshBreak> harshBreakEventsList = getAllHarshBreaks();

		// sort the list ascending
		Collections.sort(harshBreakEventsList, Comparator.comparingLong(HarshBreak::getTimestamp));

		HarshBreak currentHarshBreakEvent;

		for (int i = 0; i < harshBreakEventsList.size(); i++)
		{
			currentHarshBreakEvent = harshBreakEventsList.get(i);
			if(currentHarshBreakEvent.getTimestamp() <= olderThanUnixTimestamp)
			{
				System.out.println("Deleting harsh break event with id " + currentHarshBreakEvent.getId());
				deleteHarshBreak(currentHarshBreakEvent.getId());
			}
			else
			{
				System.out.println("Reached event newer than limit, no more harsh break events to delete!");
				break;
			}
		}
	}
	
}
