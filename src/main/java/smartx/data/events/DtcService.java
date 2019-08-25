package smartx.data.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import smartx.ui.forms.TableFormDtc;
import smartx.ui.forms.TableFormMostRecentEvents;
import smartx.utils.Convertor;


@Service
public class DtcService {

	@Autowired 
	private DtcRepository dtcRepository;

	public Dtc addDtc(Dtc dtc) {
		return dtcRepository.save(dtc);
	}

	public Dtc updateDtc(Dtc dtc) {
		return dtcRepository.save(dtc);
	}

	public Boolean deleteDtc(int id) {
		Boolean deletedSuccessfully = false;
		if (dtcRepository.existsById(id)) {
			dtcRepository.deleteById(id);
			deletedSuccessfully = true;
		}
		return deletedSuccessfully;
	}

	public void deleteAllDtcsForVehicle(int id) {
		List<Dtc> allDtcs = getAllDtcsUnorderedByVehicleId(id);
		for (Dtc dtc : allDtcs) {
			dtcRepository.deleteById(dtc.getId());
		}
	}

	public Dtc getDtcById(int id) {
		return dtcRepository.findById(id).get();
	}

	public List<Dtc> getAllDtcsUnorderedByVehicleId(int vehicleId) {
		return dtcRepository.findByVehicleId(vehicleId);
	}

	public List<Dtc> getAllDtcsOrderedDescendingByVehicleId(int vehicleId) {
		return dtcRepository.findByVehicleIdOrderByTimestampDesc(vehicleId);
	}

	public Dtc getMostRecentDtcByVehicleId(int vehicleId) {
		return dtcRepository.findFirstByVehicleIdOrderByTimestampDesc(vehicleId);
	}

	public List<Dtc> getAllDtcs() {
		List<Dtc> dtcs = new ArrayList<>();
		dtcRepository.findAll().forEach(dtcs::add);
		return dtcs;
	}

	public TableFormMostRecentEvents getMostRecentDtcEventDetailsTableForVehicle(int vehicleId, String vehicleNumber) {

		// if no event is found, we should return null
				TableFormMostRecentEvents latestEventTableForm = null;

		// retrieve the most recent dtc event for this vehicle
				Dtc mostRecentDtcEvent = getMostRecentDtcByVehicleId(vehicleId);

				if(mostRecentDtcEvent != null)
				{
					// create TableFormMostRecentEvents containing information from the most recent dtc event
					latestEventTableForm = new TableFormMostRecentEvents();
					latestEventTableForm.setTimestamp(mostRecentDtcEvent.getTimestamp());
					latestEventTableForm.setLocalDateTime(Convertor.convertTimestamp(mostRecentDtcEvent.getTimestamp()));
					latestEventTableForm.setLatitude(Convertor.convertCoordinates(mostRecentDtcEvent.getRawLatitude()));
					latestEventTableForm.setLongitude(Convertor.convertCoordinates(mostRecentDtcEvent.getRawLongitude()));
					latestEventTableForm.setVehicleNumber(vehicleNumber);
					latestEventTableForm.setTypeName(EventType.DTC.getName());
					latestEventTableForm.setTypeValue(EventType.DTC.getValue());
				}

				return latestEventTableForm;
	}

	public List<TableFormDtc> getDtcTableForms(final List<Dtc> dtcs) {

		// initialize events table list
		List<TableFormDtc> listOfDtcsTableForms = new ArrayList<TableFormDtc>();
		TableFormDtc currentTableFormDtc = null;

		// for each dtc event, create a TableFormDtc object, containing all the necessary information
		// for FE, converted so that it is displayed properly (converted latitude, converted longitude, converted timestamp)
		for (Dtc dtcEvent : dtcs)
		{
			// create TableFormDtc containing information from the dtc event
			currentTableFormDtc = new TableFormDtc();
			currentTableFormDtc.setConvertedTimestamp(Convertor.convertTimestamp(dtcEvent.getTimestamp()));
			currentTableFormDtc.setConvertedLatitude(Convertor.convertCoordinates(dtcEvent.getRawLatitude()));
			currentTableFormDtc.setConvertedLongitude(Convertor.convertCoordinates(dtcEvent.getRawLongitude()));
			currentTableFormDtc.setValue(dtcEvent.getValue());
			currentTableFormDtc.setLampStatus(dtcEvent.getLampStatus());

			listOfDtcsTableForms.add(currentTableFormDtc);
		}

		return listOfDtcsTableForms;
	}

	public void performEventsCleanup(long olderThanUnixTimestamp)
	{
		// get all dtc events from db, for all the vehicles
		List<Dtc> dtcEventsList = getAllDtcs();

		// sort the list ascending
		Collections.sort(dtcEventsList, Comparator.comparingLong(Dtc::getTimestamp));

		Dtc currentDtcEvent;

		for (int i = 0; i < dtcEventsList.size(); i++)
		{
			currentDtcEvent = dtcEventsList.get(i);
			if(currentDtcEvent.getTimestamp() <= olderThanUnixTimestamp)
			{
				System.out.println("Deleting dtc event with id " + currentDtcEvent.getId());
				deleteDtc(currentDtcEvent.getId());
			}
			else
			{
				System.out.println("Reached event newer than limit, no more dtc events to delete!");
				break;
			}
		}
	}

}
