package smartx.data.events;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import smartx.businesslogic.vehicletracking.GMapsCoordinates;
import smartx.data.vehicle.Vehicle;
import smartx.ui.forms.TableFormMostRecentEvents;
import smartx.ui.forms.TableFormVehicleTracking;
import smartx.utils.Convertor;


@Service
public class VehicleTrackingService {

	@Autowired 
	private VehicleTrackingRepository vehicleTrackingRepository;

	public VehicleTracking addVehicleTracking(VehicleTracking vehicleTracking) {
		return vehicleTrackingRepository.save(vehicleTracking);
	}

	public VehicleTracking updateVehicleTracking(VehicleTracking vehicleTracking) {
		return vehicleTrackingRepository.save(vehicleTracking);
	}

	public Boolean deleteVehicleTracking(int id) {
		Boolean deletedSuccessfully = false;
		if (vehicleTrackingRepository.existsById(id)) {
			vehicleTrackingRepository.deleteById(id);
			deletedSuccessfully = true;
		}
		return deletedSuccessfully;
	}

	public void deleteAllVehicleTrackingsForVehicle(int id) {
		List<VehicleTracking> allVehicleTrackings = getAllVehicleTrackingsUnorderedByVehicleId(id);
		for (VehicleTracking vehicleTracking : allVehicleTrackings) {
			vehicleTrackingRepository.deleteById(vehicleTracking.getId());
		}
	}

	public VehicleTracking getVehicleTrackingById(int id) {
		return vehicleTrackingRepository.findById(id).get();
	}

	public List<VehicleTracking> getAllVehicleTrackingsUnorderedByVehicleId(int vehicleId) {
		return vehicleTrackingRepository.findByVehicleId(vehicleId);
	}

	public List<VehicleTracking> getAllVehicleTrackingsOrderedDescendingByVehicleId(int vehicleId) {
		return vehicleTrackingRepository.findByVehicleIdOrderByTimestampDesc(vehicleId);
	}

	public VehicleTracking getMostRecentVehicleTrackingByVehicleId(int vehicleId) {
		return vehicleTrackingRepository.findFirstByVehicleIdOrderByTimestampDesc(vehicleId);
	}

	public List<VehicleTracking> getAllVehicleTrackings() {
		List<VehicleTracking> vehicleTrackings = new ArrayList<>();
		vehicleTrackingRepository.findAll().forEach(vehicleTrackings::add);
		return vehicleTrackings;
	}

	public List<VehicleTracking> getVehicleTrackingsByVehicleIdAndTimestamp(int vehicleId, final LocalDate timestampFilter) {

		List<VehicleTracking> vehicleTrackings = getAllVehicleTrackingsOrderedDescendingByVehicleId(vehicleId);
		List<VehicleTracking> filteredVehicleTrackings = new ArrayList<>();

		System.out.println("Vehicle trackings size: "+ vehicleTrackings.size());
		System.out.println("Timestamp: " + timestampFilter);

		LocalDate vehicleTrackingLocalDate;
		Date convertedTimestamp;

		for (int i = 0; i < vehicleTrackings.size(); i++)
		{
			// To convert to Java Date from UNIX timestamp:
			// java.util.Date time=new java.util.Date((long)timeStamp*1000);
			//
			// If you already had milliseconds, then just new java.util.Date((long)timeStamp);
			convertedTimestamp = new java.util.Date((long)vehicleTrackings.get(i).getTimestamp() * 1000);
			// convertedTimestamp constructed above uses the system default (RO) zone ID => UTC + 2
			// when converting to LocalDate, we should specify UTC zone id => UTC + 0 date
			// this way, both timestampFilter and vehicleTrackingLocalDate are in UTC 
			vehicleTrackingLocalDate = convertedTimestamp.toInstant().atZone(TimeZone.getTimeZone("GMT").toZoneId()).toLocalDate();
			System.out.println("Converted timestamp: " + convertedTimestamp);

			if(vehicleTrackingLocalDate.isAfter(timestampFilter))
			{
				System.out.println("Continue!");
				continue;
			}
			else if (vehicleTrackingLocalDate.isEqual(timestampFilter))
			{
				System.out.println("Add!");
				filteredVehicleTrackings.add(vehicleTrackings.get(i));
			}
			else
			{
				System.out.println("Break!");
				break;
			}
		}

		return filteredVehicleTrackings;
	}

	/**
	 * The method creates a list of GMapsCoordinates from a list of vehicle tracking events. 
	 * @param vehicleTrackings - The list of vehicle tracking events to be converted.
	 * @return A list of GMapsCoordinates for use with Google Maps. An empty list is returned if there is no tracking event for the
	 * vehicle and in the current timestamp.
	 * @see GMapsCoordinates
	 */
	public List<GMapsCoordinates> getGoogleMapsCoordinatesFroVehicleTrackingEvents(final List<VehicleTracking> vehicleTrackings) {
		final List<GMapsCoordinates> gMapsCoordinates = new ArrayList<>();

		for (VehicleTracking vehicleTracking : vehicleTrackings) {
			GMapsCoordinates googleMapsCoordinate =  new GMapsCoordinates();
			googleMapsCoordinate.setLatitude(Convertor.convertCoordinates(vehicleTracking.getRawLatitude()));
			googleMapsCoordinate.setLongitude(Convertor.convertCoordinates(vehicleTracking.getRawLongitude()));

			final Vehicle vehicle = vehicleTracking.getVehicle();
			final String vehicleStatus = vehicle.getStatus();
			googleMapsCoordinate.setStatus(vehicleStatus);

			gMapsCoordinates.add(googleMapsCoordinate);
		}

		return gMapsCoordinates;
	}

	public TableFormMostRecentEvents getMostRecentVehicleTrackingEventDetailsTableForVehicle(int vehicleId, String vehicleNumber) {

		// if no event is found, we should return null
		TableFormMostRecentEvents latestEventTableForm = null;

		// retrieve the most recent vehicle tracking event for this vehicle
		VehicleTracking mostRecentVehicleTrackingEvent = getMostRecentVehicleTrackingByVehicleId(vehicleId);

		if(mostRecentVehicleTrackingEvent != null)
		{
			// create TableFormMostRecentEvents containing information from the most recent vehicle tracking event
			latestEventTableForm = new TableFormMostRecentEvents();
			latestEventTableForm.setTimestamp(mostRecentVehicleTrackingEvent.getTimestamp());
			latestEventTableForm.setLocalDateTime(Convertor.convertTimestamp(mostRecentVehicleTrackingEvent.getTimestamp()));
			latestEventTableForm.setLatitude(Convertor.convertCoordinates(mostRecentVehicleTrackingEvent.getRawLatitude()));
			latestEventTableForm.setLongitude(Convertor.convertCoordinates(mostRecentVehicleTrackingEvent.getRawLongitude()));
			latestEventTableForm.setVehicleNumber(vehicleNumber);
			latestEventTableForm.setTypeName(EventType.VEHICLE_TRACKING.getName());
			latestEventTableForm.setTypeValue(EventType.VEHICLE_TRACKING.getValue());
		}

		return latestEventTableForm;
	}

	public List<TableFormVehicleTracking> getVehicleTrackingTableForms(final List<VehicleTracking> vehicleTrackings) {

		// initialize events table list
		List<TableFormVehicleTracking> listOfVehicleTrackingsTableForms = new ArrayList<TableFormVehicleTracking>();
		TableFormVehicleTracking currentTableFormVehicleTracking = null;

		// for each vehicle tracking event, create a TableFormVehicleTracking object, containing all the necessary information
		// for FE, converted so that it is displayed properly (converted latitude, converted longitude, converted timestamp)
		for (VehicleTracking vehicleTrackingEvent : vehicleTrackings)
		{
			// create TableFormVehicleTracking containing information from the vehicle tracking event
			currentTableFormVehicleTracking = new TableFormVehicleTracking();
			currentTableFormVehicleTracking.setConvertedTimestamp(Convertor.convertTimestamp(vehicleTrackingEvent.getTimestamp()));
			currentTableFormVehicleTracking.setConvertedLatitude(Convertor.convertCoordinates(vehicleTrackingEvent.getRawLatitude()));
			currentTableFormVehicleTracking.setConvertedLongitude(Convertor.convertCoordinates(vehicleTrackingEvent.getRawLongitude()));
			currentTableFormVehicleTracking.setHeading(vehicleTrackingEvent.getHeading());
			
			listOfVehicleTrackingsTableForms.add(currentTableFormVehicleTracking);
		}

		return listOfVehicleTrackingsTableForms;
	}

	public void performEventsCleanup(long olderThanUnixTimestamp)
	{
		// get all vehicle tracking events from db, for all the vehicles
		List<VehicleTracking> vehicleTrackingEventsList = getAllVehicleTrackings();

		// sort the list ascending
		Collections.sort(vehicleTrackingEventsList, Comparator.comparingLong(VehicleTracking::getTimestamp));

		VehicleTracking currentVehicleTrackingEvent;

		for (int i = 0; i < vehicleTrackingEventsList.size(); i++)
		{
			currentVehicleTrackingEvent = vehicleTrackingEventsList.get(i);
			if(currentVehicleTrackingEvent.getTimestamp() <= olderThanUnixTimestamp)
			{
				System.out.println("Deleting vehicle tracking event with id " + currentVehicleTrackingEvent.getId());
				deleteVehicleTracking(currentVehicleTrackingEvent.getId());
			}
			else
			{
				System.out.println("Reached event newer than limit, no more vehicle tracking events to delete!");
				break;
			}
		}
	}
}
