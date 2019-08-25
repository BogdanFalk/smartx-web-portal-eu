package smartx.data.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import smartx.ui.forms.TableFormBorderCrossing;
import smartx.ui.forms.TableFormMostRecentEvents;
import smartx.utils.Convertor;


@Service
public class BorderCrossingService {

	@Autowired 
	private BorderCrossingRepository borderCrossingRepository;

	public BorderCrossing addBorderCrossing(BorderCrossing borderCrossing) {
		return borderCrossingRepository.save(borderCrossing);
	}

	public BorderCrossing updateBorderCrossing(BorderCrossing borderCrossing) {
		return borderCrossingRepository.save(borderCrossing);
	}

	public Boolean deleteBorderCrossing(int id) {
		Boolean deletedSuccessfully = false;
		if (borderCrossingRepository.existsById(id)) {
			borderCrossingRepository.deleteById(id);
			deletedSuccessfully = true;
		}
		return deletedSuccessfully;
	}

	public void deleteAllBorderCrossingsForVehicle(int id) {
		List<BorderCrossing> allBorderCrossings = getAllBorderCrossingsUnorderedByVehicleId(id);
		for (BorderCrossing borderCrossing : allBorderCrossings) {
			borderCrossingRepository.deleteById(borderCrossing.getId());
		}
	}

	public BorderCrossing getBorderCrossingById(int id) {
		return borderCrossingRepository.findById(id).get();
	}

	public List<BorderCrossing> getAllBorderCrossingsUnorderedByVehicleId(int vehicleId) {
		return borderCrossingRepository.findByVehicleId(vehicleId);
	}

	public List<BorderCrossing> getAllBorderCrossingsOrderedDescendingByVehicleId(int vehicleId) {
		return borderCrossingRepository.findByVehicleIdOrderByTimestampDesc(vehicleId);
	}

	public BorderCrossing getMostRecentBorderCrossingByVehicleId(int vehicleId) {
		return borderCrossingRepository.findFirstByVehicleIdOrderByTimestampDesc(vehicleId);
	}

	public List<BorderCrossing> getAllBorderCrossings() {
		List<BorderCrossing> borderCrossings = new ArrayList<>();
		borderCrossingRepository.findAll().forEach(borderCrossings::add);
		return borderCrossings;
	}

	public TableFormMostRecentEvents getMostRecentBorderCrossingEventDetailsTableForVehicle(int vehicleId, String vehicleNumber) {

		// if no event is found, we should return null
		TableFormMostRecentEvents latestEventTableForm = null;

		// retrieve the most recent border crossing event for this vehicle
		BorderCrossing mostRecentBorderCrossingEvent = getMostRecentBorderCrossingByVehicleId(vehicleId);

		if(mostRecentBorderCrossingEvent != null)
		{
			// create TableFormMostRecentEvents containing information from the most recent border crossing event
			latestEventTableForm = new TableFormMostRecentEvents();
			latestEventTableForm.setTimestamp(mostRecentBorderCrossingEvent.getTimestamp());
			latestEventTableForm.setLocalDateTime(Convertor.convertTimestamp(mostRecentBorderCrossingEvent.getTimestamp()));
			latestEventTableForm.setLatitude(Convertor.convertCoordinates(mostRecentBorderCrossingEvent.getRawLatitude()));
			latestEventTableForm.setLongitude(Convertor.convertCoordinates(mostRecentBorderCrossingEvent.getRawLongitude()));
			latestEventTableForm.setVehicleNumber(vehicleNumber);
			latestEventTableForm.setTypeName(EventType.BORDER_CROSSING.getName());
			latestEventTableForm.setTypeValue(EventType.BORDER_CROSSING.getValue());
		}

		return latestEventTableForm;
	}

	public List<TableFormBorderCrossing> getBorderCrossingTableForms(final List<BorderCrossing> borderCrossings) {

		// initialize events table list
		List<TableFormBorderCrossing> listOfBorderCrossingsTableForms = new ArrayList<TableFormBorderCrossing>();
		TableFormBorderCrossing currentTableFormBorderCrossing = null;

		// for each border crossing event, create a TableFormBorderCrossing object, containing all the necessary information
		// for FE, converted so that it is displayed properly (converted latitude, converted longitude, converted timestamp)
		for (BorderCrossing borderCrossingEvent : borderCrossings)
		{
			// create TableFormBorderCrossing containing information from the border crossing event
			currentTableFormBorderCrossing = new TableFormBorderCrossing();
			currentTableFormBorderCrossing.setConvertedTimestamp(Convertor.convertTimestamp(borderCrossingEvent.getTimestamp()));
			currentTableFormBorderCrossing.setConvertedLatitude(Convertor.convertCoordinates(borderCrossingEvent.getRawLatitude()));
			currentTableFormBorderCrossing.setConvertedLongitude(Convertor.convertCoordinates(borderCrossingEvent.getRawLongitude()));
			currentTableFormBorderCrossing.setOldCountry(borderCrossingEvent.getOldCountry());
			currentTableFormBorderCrossing.setNewCountry(borderCrossingEvent.getNewCountry());
			
			listOfBorderCrossingsTableForms.add(currentTableFormBorderCrossing);
		}

		return listOfBorderCrossingsTableForms;
	}
	
	public void performEventsCleanup(long olderThanUnixTimestamp)
	{
		// get all border crossing events from db, for all the vehicles
		List<BorderCrossing> borderCrossingEventsList = getAllBorderCrossings();

		// sort the list ascending
		Collections.sort(borderCrossingEventsList, Comparator.comparingLong(BorderCrossing::getTimestamp));

		BorderCrossing currentBorderCrossingEvent;

		for (int i = 0; i < borderCrossingEventsList.size(); i++)
		{
			currentBorderCrossingEvent = borderCrossingEventsList.get(i);
			if(currentBorderCrossingEvent.getTimestamp() <= olderThanUnixTimestamp)
			{
				System.out.println("Deleting border crossing event with id " + currentBorderCrossingEvent.getId());
				deleteBorderCrossing(currentBorderCrossingEvent.getId());
			}
			else
			{
				System.out.println("Reached event newer than limit, no more border crossing events to delete!");
				break;
			}
		}
	}

}
