package smartx.data.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import smartx.ui.forms.TableFormMostRecentEvents;
import smartx.ui.forms.TableFormTireCondition;
import smartx.utils.Convertor;


@Service
public class TireConditionService {

	@Autowired 
	private TireConditionRepository tireConditionRepository;

	public TireCondition addTireCondition(TireCondition tireCondition) {
		return tireConditionRepository.save(tireCondition);
	}

	public TireCondition updateTireCondition(TireCondition tireCondition) {
		return tireConditionRepository.save(tireCondition);
	}

	public Boolean deleteTireCondition(int id) {
		Boolean deletedSuccessfully = false;
		if (tireConditionRepository.existsById(id)) {
			tireConditionRepository.deleteById(id);
			deletedSuccessfully = true;
		}
		return deletedSuccessfully;
	}

	public void deleteAllTireConditionsForVehicle(int id) {
		List<TireCondition> allTireConditions = getAllTireConditionsUnorderedByVehicleId(id);
		for (TireCondition tireCondition : allTireConditions) {
			tireConditionRepository.deleteById(tireCondition.getId());
		}
	}

	public TireCondition getTireConditionById(int id) {
		return tireConditionRepository.findById(id).get();
	}

	public List<TireCondition> getAllTireConditionsUnorderedByVehicleId(int vehicleId) {
		return tireConditionRepository.findByVehicleId(vehicleId);
	}

	public List<TireCondition> getAllTireConditionsOrderedDescendingByVehicleId(int vehicleId) {
		return tireConditionRepository.findByVehicleIdOrderByTimestampDesc(vehicleId);
	}

	public TireCondition getMostRecentTireConditionByVehicleId(int vehicleId) {
		return tireConditionRepository.findFirstByVehicleIdOrderByTimestampDesc(vehicleId);
	}

	public List<TireCondition> getAllTireConditions() {
		List<TireCondition> tireConditions = new ArrayList<>();
		tireConditionRepository.findAll().forEach(tireConditions::add);
		return tireConditions;
	}

	public TableFormMostRecentEvents getMostRecentTireConditionEventDetailsTableForVehicle(int vehicleId, String vehicleNumber) {

		// if no event is found, we should return null
		TableFormMostRecentEvents latestEventTableForm = null;

		// retrieve the most recent tire condition event for this vehicle
		TireCondition mostRecentTireConditionEvent = getMostRecentTireConditionByVehicleId(vehicleId);

		if(mostRecentTireConditionEvent != null)
		{
			// create TableFormMostRecentEvents containing information from the most recent tire condition event
			latestEventTableForm = new TableFormMostRecentEvents();
			latestEventTableForm.setTimestamp(mostRecentTireConditionEvent.getTimestamp());
			latestEventTableForm.setLocalDateTime(Convertor.convertTimestamp(mostRecentTireConditionEvent.getTimestamp()));
			latestEventTableForm.setLatitude(Convertor.convertCoordinates(mostRecentTireConditionEvent.getRawLatitude()));
			latestEventTableForm.setLongitude(Convertor.convertCoordinates(mostRecentTireConditionEvent.getRawLongitude()));
			latestEventTableForm.setVehicleNumber(vehicleNumber);
			latestEventTableForm.setTypeName(EventType.TIRE_CONDITION.getName());
			latestEventTableForm.setTypeValue(EventType.TIRE_CONDITION.getValue());
		}

		return latestEventTableForm;
	}

	public List<TableFormTireCondition> getTireConditionTableForms(final List<TireCondition> tireConditions) {

		// initialize events table list
		List<TableFormTireCondition> listOfTireConditionsTableForms = new ArrayList<TableFormTireCondition>();
		TableFormTireCondition currentTableFormTireCondition = null;

		// for each tire condition event, create a TableFormTireCondition object, containing all the necessary information
		// for FE, converted so that it is displayed properly (converted latitude, converted longitude, converted timestamp)
		for (TireCondition tireConditionEvent : tireConditions)
		{
			// create TableFormTireCondition containing information from the tire condition event
			currentTableFormTireCondition = new TableFormTireCondition();
			currentTableFormTireCondition.setConvertedTimestamp(Convertor.convertTimestamp(tireConditionEvent.getTimestamp()));
			currentTableFormTireCondition.setConvertedLatitude(Convertor.convertCoordinates(tireConditionEvent.getRawLatitude()));
			currentTableFormTireCondition.setConvertedLongitude(Convertor.convertCoordinates(tireConditionEvent.getRawLongitude()));
			currentTableFormTireCondition.setPressureValue(tireConditionEvent.getPressureValue());
			currentTableFormTireCondition.setPressureType(tireConditionEvent.getPressureType());
			currentTableFormTireCondition.setTirePosition(tireConditionEvent.getTirePosition());
			currentTableFormTireCondition.setTemperatureValue(tireConditionEvent.getTemperatureValue());

			listOfTireConditionsTableForms.add(currentTableFormTireCondition);
		}

		return listOfTireConditionsTableForms;
	}

	public void performEventsCleanup(long olderThanUnixTimestamp)
	{
		// get all tire condition events from db, for all the vehicles
		List<TireCondition> tireConditionEventsList = getAllTireConditions();

		// sort the list ascending
		Collections.sort(tireConditionEventsList, Comparator.comparingLong(TireCondition::getTimestamp));

		TireCondition currentTireConditionEvent;

		for (int i = 0; i < tireConditionEventsList.size(); i++)
		{
			currentTireConditionEvent = tireConditionEventsList.get(i);
			if(currentTireConditionEvent.getTimestamp() <= olderThanUnixTimestamp)
			{
				System.out.println("Deleting tire condition event with id " + currentTireConditionEvent.getId());
				deleteTireCondition(currentTireConditionEvent.getId());
			}
			else
			{
				System.out.println("Reached event newer than limit, no more tire condition events to delete!");
				break;
			}
		}
	}

}
