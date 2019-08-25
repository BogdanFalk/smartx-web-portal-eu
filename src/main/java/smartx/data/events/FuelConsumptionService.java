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
public class FuelConsumptionService {

	@Autowired 
	private FuelConsumptionRepository fuelConsumptionRepository;

	public FuelConsumption addFuelConsumption(FuelConsumption fuelConsumption) {
		return fuelConsumptionRepository.save(fuelConsumption);
	}

	public FuelConsumption updateFuelConsumption(FuelConsumption fuelConsumption) {
		return fuelConsumptionRepository.save(fuelConsumption);
	}

	public Boolean deleteFuelConsumption(int id) {
		Boolean deletedSuccessfully = false;
		if (fuelConsumptionRepository.existsById(id)) {
			fuelConsumptionRepository.deleteById(id);
			deletedSuccessfully = true;
		}
		return deletedSuccessfully;
	}

	public void deleteAllFuelConsumptionsForVehicle(int id) {
		List<FuelConsumption> allFuelConsumptions = getAllFuelConsumptionsUnorderedByVehicleId(id);
		for (FuelConsumption fuelConsumption : allFuelConsumptions) {
			fuelConsumptionRepository.deleteById(fuelConsumption.getId());
		}
	}

	public FuelConsumption getFuelConsumptionById(int id) {
		return fuelConsumptionRepository.findById(id).get();
	}

	public List<FuelConsumption> getAllFuelConsumptionsUnorderedByVehicleId(int vehicleId) {
		return fuelConsumptionRepository.findByVehicleId(vehicleId);
	}

	public List<FuelConsumption> getAllFuelConsumptionsOrderedDescendingByVehicleId(int vehicleId) {
		return fuelConsumptionRepository.findByVehicleIdOrderByTimestampDesc(vehicleId);
	}

	public FuelConsumption getMostRecentFuelConsumptionByVehicleId(int vehicleId) {
		return fuelConsumptionRepository.findFirstByVehicleIdOrderByTimestampDesc(vehicleId);
	}

	public List<FuelConsumption> getAllFuelConsumptions() {
		List<FuelConsumption> fuelConsumptions = new ArrayList<>();
		fuelConsumptionRepository.findAll().forEach(fuelConsumptions::add);
		return fuelConsumptions;
	}

	public TableFormMostRecentEvents getMostRecentFuelConsumptionEventDetailsTableForVehicle(int vehicleId, String vehicleNumber) {

		// if no event is found, we should return null
				TableFormMostRecentEvents latestEventTableForm = null;

		// retrieve the most recent fuel consumption event for this vehicle
				FuelConsumption mostRecentFuelConsumptionEvent = getMostRecentFuelConsumptionByVehicleId(vehicleId);

				if(mostRecentFuelConsumptionEvent != null)
				{
					// create TableFormMostRecentEvents containing information from the most recent fuel consumption event
					latestEventTableForm = new TableFormMostRecentEvents();
					latestEventTableForm.setTimestamp(mostRecentFuelConsumptionEvent.getTimestamp());
					latestEventTableForm.setLocalDateTime(Convertor.convertTimestamp(mostRecentFuelConsumptionEvent.getTimestamp()));
					latestEventTableForm.setLatitude(Convertor.convertCoordinates(mostRecentFuelConsumptionEvent.getRawLatitude()));
					latestEventTableForm.setLongitude(Convertor.convertCoordinates(mostRecentFuelConsumptionEvent.getRawLongitude()));
					latestEventTableForm.setVehicleNumber(vehicleNumber);
					latestEventTableForm.setTypeName(EventType.FUEL_CONSUMPTION.getName());
					latestEventTableForm.setTypeValue(EventType.FUEL_CONSUMPTION.getValue());
				}

				return latestEventTableForm;
	}

	public List<TableFormFuelHarsh> getFuelConsumptionTableForms(final List<FuelConsumption> fuelConsumptions) {

		// initialize events table list
		List<TableFormFuelHarsh> listOfFuelConsumptionsTableForms = new ArrayList<TableFormFuelHarsh>();
		TableFormFuelHarsh currentTableFormFuelConsumption = null;

		// for each fuel consumption event, create a TableFormFuelHarsh object, containing all the necessary information
		// for FE, converted so that it is displayed properly (converted latitude, converted longitude, converted timestamp)
		for (FuelConsumption fuelConsumptionEvent : fuelConsumptions)
		{
			// create TableFormFuelHarsh containing information from the fuel consumption event
			currentTableFormFuelConsumption = new TableFormFuelHarsh();
			currentTableFormFuelConsumption.setConvertedTimestamp(Convertor.convertTimestamp(fuelConsumptionEvent.getTimestamp()));
			currentTableFormFuelConsumption.setConvertedLatitude(Convertor.convertCoordinates(fuelConsumptionEvent.getRawLatitude()));
			currentTableFormFuelConsumption.setConvertedLongitude(Convertor.convertCoordinates(fuelConsumptionEvent.getRawLongitude()));
			currentTableFormFuelConsumption.setValue(fuelConsumptionEvent.getValue());

			listOfFuelConsumptionsTableForms.add(currentTableFormFuelConsumption);
		}

		return listOfFuelConsumptionsTableForms;
	}

	public void performEventsCleanup(long olderThanUnixTimestamp)
	{
		// get all fuel consumption events from db, for all the vehicles
		List<FuelConsumption> fuelConsumptionEventsList = getAllFuelConsumptions();

		// sort the list ascending
		Collections.sort(fuelConsumptionEventsList, Comparator.comparingLong(FuelConsumption::getTimestamp));

		FuelConsumption currentFuelConsumptionEvent;

		for (int i = 0; i < fuelConsumptionEventsList.size(); i++)
		{
			currentFuelConsumptionEvent = fuelConsumptionEventsList.get(i);
			if(currentFuelConsumptionEvent.getTimestamp() <= olderThanUnixTimestamp)
			{
				System.out.println("Deleting fuel consumption event with id " + currentFuelConsumptionEvent.getId());
				deleteFuelConsumption(currentFuelConsumptionEvent.getId());
			}
			else
			{
				System.out.println("Reached event newer than limit, no more fuel consumption events to delete!");
				break;
			}
		}
	}

}
