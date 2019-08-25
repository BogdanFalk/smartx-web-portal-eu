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
public class OverSpeedService {

	@Autowired 
	private OverSpeedRepository overSpeedRepository;

	public OverSpeed addOverSpeed(OverSpeed overSpeed) {
		return overSpeedRepository.save(overSpeed);
	}

	public OverSpeed updateOverSpeed(OverSpeed overSpeed) {
		return overSpeedRepository.save(overSpeed);
	}

	public Boolean deleteOverSpeed(int id) {
		Boolean deletedSuccessfully = false;
		if (overSpeedRepository.existsById(id)) {
			overSpeedRepository.deleteById(id);
			deletedSuccessfully = true;
		}
		return deletedSuccessfully;
	}

	public void deleteAllOverSpeedsForVehicle(int id) {
		List<OverSpeed> allOverSpeeds = getAllOverSpeedsUnorderedByVehicleId(id);
		for (OverSpeed overSpeed : allOverSpeeds) {
			overSpeedRepository.deleteById(overSpeed.getId());
		}
	}

	public OverSpeed getOverSpeedById(int id) {
		return overSpeedRepository.findById(id).get();
	}

	public List<OverSpeed> getAllOverSpeedsUnorderedByVehicleId(int vehicleId) {
		return overSpeedRepository.findByVehicleId(vehicleId);
	}

	public List<OverSpeed> getAllOverSpeedsOrderedDescendingByVehicleId(int vehicleId) {
		return overSpeedRepository.findByVehicleIdOrderByTimestampDesc(vehicleId);
	}

	public OverSpeed getMostRecentOverSpeedByVehicleId(int vehicleId) {
		return overSpeedRepository.findFirstByVehicleIdOrderByTimestampDesc(vehicleId);
	}

	public List<OverSpeed> getAllOverSpeeds() {
		List<OverSpeed> overSpeeds = new ArrayList<>();
		overSpeedRepository.findAll().forEach(overSpeeds::add);
		return overSpeeds;
	}

	public TableFormMostRecentEvents getMostRecentOverSpeedEventDetailsTableForVehicle(int vehicleId, String vehicleNumber) {

		// if no event is found, we should return null
		TableFormMostRecentEvents latestEventTableForm = null;

		// retrieve the most recent over speed event for this vehicle
		OverSpeed mostRecentOverSpeedEvent = getMostRecentOverSpeedByVehicleId(vehicleId);

		if(mostRecentOverSpeedEvent != null)
		{
			// create TableFormMostRecentEvents containing information from the most recent over speed event
			latestEventTableForm = new TableFormMostRecentEvents();
			latestEventTableForm.setTimestamp(mostRecentOverSpeedEvent.getTimestamp());
			latestEventTableForm.setLocalDateTime(Convertor.convertTimestamp(mostRecentOverSpeedEvent.getTimestamp()));
			latestEventTableForm.setLatitude(Convertor.convertCoordinates(mostRecentOverSpeedEvent.getRawLatitude()));
			latestEventTableForm.setLongitude(Convertor.convertCoordinates(mostRecentOverSpeedEvent.getRawLongitude()));
			latestEventTableForm.setVehicleNumber(vehicleNumber);
			latestEventTableForm.setTypeName(EventType.OVER_SPEED.getName());
			latestEventTableForm.setTypeValue(EventType.OVER_SPEED.getValue());
		}

		return latestEventTableForm;
	}
	
	public List<TableFormOver> getOverSpeedTableForms(final List<OverSpeed> overSpeeds) {

		// initialize events table list
		List<TableFormOver> listOfOverSpeedsTableForms = new ArrayList<TableFormOver>();
		TableFormOver currentTableFormOverSpeed = null;

		// for each over speed event, create a TableFormOver object, containing all the necessary information
		// for FE, converted so that it is displayed properly (converted latitude, converted longitude, converted timestamp)
		for (OverSpeed overSpeedEvent : overSpeeds)
		{
			// create TableFormOver containing information from the over speed event
			currentTableFormOverSpeed = new TableFormOver();
			currentTableFormOverSpeed.setConvertedTimestamp(Convertor.convertTimestamp(overSpeedEvent.getTimestamp()));
			currentTableFormOverSpeed.setConvertedLatitude(Convertor.convertCoordinates(overSpeedEvent.getRawLatitude()));
			currentTableFormOverSpeed.setConvertedLongitude(Convertor.convertCoordinates(overSpeedEvent.getRawLongitude()));
			currentTableFormOverSpeed.setValue(overSpeedEvent.getValue());
			currentTableFormOverSpeed.setDuration(overSpeedEvent.getDuration());

			listOfOverSpeedsTableForms.add(currentTableFormOverSpeed);
		}

		return listOfOverSpeedsTableForms;
	}

	public void performEventsCleanup(long olderThanUnixTimestamp)
	{
		// get all over speed events from db, for all the vehicles
		List<OverSpeed> overSpeedEventsList = getAllOverSpeeds();

		// sort the list ascending
		Collections.sort(overSpeedEventsList, Comparator.comparingLong(OverSpeed::getTimestamp));

		OverSpeed currentOverSpeedEvent;

		for (int i = 0; i < overSpeedEventsList.size(); i++)
		{
			currentOverSpeedEvent = overSpeedEventsList.get(i);
			if(currentOverSpeedEvent.getTimestamp() <= olderThanUnixTimestamp)
			{
				System.out.println("Deleting over speed event with id " + currentOverSpeedEvent.getId());
				deleteOverSpeed(currentOverSpeedEvent.getId());
			}
			else
			{
				System.out.println("Reached event newer than limit, no more over speed events to delete!");
				break;
			}
		}
	}

}
