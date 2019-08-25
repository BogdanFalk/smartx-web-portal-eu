package smartx.data.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import smartx.ui.forms.TableFormEngineStatus;
import smartx.ui.forms.TableFormMostRecentEvents;
import smartx.utils.Convertor;


@Service
public class EngineStatusService {

	@Autowired 
	private EngineStatusRepository engineStatusRepository;

	public EngineStatus addEngineStatus(EngineStatus engineStatus) {
		return engineStatusRepository.save(engineStatus);
	}

	public EngineStatus updateEngineStatus(EngineStatus engineStatus) {
		return engineStatusRepository.save(engineStatus);
	}

	public Boolean deleteEngineStatus(int id) {
		Boolean deletedSuccessfully = false;
		if (engineStatusRepository.existsById(id)) {
			engineStatusRepository.deleteById(id);
			deletedSuccessfully = true;
		}
		return deletedSuccessfully;
	}

	public void deleteAllEngineStatusesForVehicle(int id) {
		List<EngineStatus> allEngineStatuses = getAllEngineStatusesUnorderedByVehicleId(id);
		for (EngineStatus engineStatus : allEngineStatuses) {
			engineStatusRepository.deleteById(engineStatus.getId());
		}
	}

	public EngineStatus getEngineStatusById(int id) {
		return engineStatusRepository.findById(id).get();
	}

	public List<EngineStatus> getAllEngineStatusesUnorderedByVehicleId(int vehicleId) {
		return engineStatusRepository.findByVehicleId(vehicleId);
	}

	public List<EngineStatus> getAllEngineStatusesOrderedDescendingByVehicleId(int vehicleId) {
		return engineStatusRepository.findByVehicleIdOrderByTimestampDesc(vehicleId);
	}

	public EngineStatus getMostRecentEngineStatusByVehicleId(int vehicleId) {
		return engineStatusRepository.findFirstByVehicleIdOrderByTimestampDesc(vehicleId);
	}

	public List<EngineStatus> getAllEngineStatuses() {
		List<EngineStatus> engineStatuses = new ArrayList<>();
		engineStatusRepository.findAll().forEach(engineStatuses::add);
		return engineStatuses;
	}

	public TableFormMostRecentEvents getMostRecentEngineStatusEventDetailsTableForVehicle(int vehicleId, String vehicleNumber) {

		// if no event is found, we should return null
				TableFormMostRecentEvents latestEventTableForm = null;

		// retrieve the most recent engine status event for this vehicle
				EngineStatus mostRecentEngineStatusEvent = getMostRecentEngineStatusByVehicleId(vehicleId);

				if(mostRecentEngineStatusEvent != null)
				{
					// create TableFormMostRecentEvents containing information from the most recent engine status event
					latestEventTableForm = new TableFormMostRecentEvents();
					latestEventTableForm.setTimestamp(mostRecentEngineStatusEvent.getTimestamp());
					latestEventTableForm.setLocalDateTime(Convertor.convertTimestamp(mostRecentEngineStatusEvent.getTimestamp()));
					latestEventTableForm.setLatitude(Convertor.convertCoordinates(mostRecentEngineStatusEvent.getRawLatitude()));
					latestEventTableForm.setLongitude(Convertor.convertCoordinates(mostRecentEngineStatusEvent.getRawLongitude()));
					latestEventTableForm.setVehicleNumber(vehicleNumber);
					latestEventTableForm.setTypeName(EventType.ENGINE_STATUS.getName());
					latestEventTableForm.setTypeValue(EventType.ENGINE_STATUS.getValue());
				}

				return latestEventTableForm;
	}

	public List<TableFormEngineStatus> getEngineStatusTableForms(final List<EngineStatus> engineStatuses) {

		// initialize events table list
		List<TableFormEngineStatus> listOfEngineStatusesTableForms = new ArrayList<TableFormEngineStatus>();
		TableFormEngineStatus currentTableFormEngineStatus = null;

		// for each engine status event, create a TableFormEngineStatus object, containing all the necessary information
		// for FE, converted so that it is displayed properly (converted latitude, converted longitude, converted timestamp)
		for (EngineStatus engineStatusEvent : engineStatuses)
		{
			// create TableFormEngineStatus containing information from the engine status event
			currentTableFormEngineStatus = new TableFormEngineStatus();
			currentTableFormEngineStatus.setConvertedTimestamp(Convertor.convertTimestamp(engineStatusEvent.getTimestamp()));
			currentTableFormEngineStatus.setConvertedLatitude(Convertor.convertCoordinates(engineStatusEvent.getRawLatitude()));
			currentTableFormEngineStatus.setConvertedLongitude(Convertor.convertCoordinates(engineStatusEvent.getRawLongitude()));
			currentTableFormEngineStatus.setValue(engineStatusEvent.getValue());

			listOfEngineStatusesTableForms.add(currentTableFormEngineStatus);
		}

		return listOfEngineStatusesTableForms;
	}

	public void performEventsCleanup(long olderThanUnixTimestamp)
	{
		// get all engine status events from db, for all the vehicles
		List<EngineStatus> engineStatusEventsList = getAllEngineStatuses();

		// sort the list ascending
		Collections.sort(engineStatusEventsList, Comparator.comparingLong(EngineStatus::getTimestamp));

		EngineStatus currentEngineStatusEvent;

		for (int i = 0; i < engineStatusEventsList.size(); i++)
		{
			currentEngineStatusEvent = engineStatusEventsList.get(i);
			if(currentEngineStatusEvent.getTimestamp() <= olderThanUnixTimestamp)
			{
				System.out.println("Deleting engine status event with id " + currentEngineStatusEvent.getId());
				deleteEngineStatus(currentEngineStatusEvent.getId());
			}
			else
			{
				System.out.println("Reached event newer than limit, no more engine status events to delete!");
				break;
			}
		}
	}

}
