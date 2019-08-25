package smartx.ui.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import smartx.businesslogic.DeviceCommunicationController;
import smartx.businesslogic.vehicletracking.GMapsCoordinates;
import smartx.data.configuration.DeviceConfiguration;
import smartx.data.configuration.DeviceConfigurationService;
import smartx.data.configuration.VehicleConfiguration;
import smartx.data.configuration.VehicleConfigurationService;
import smartx.data.driver.Driver;
import smartx.data.driver.DriverService;
import smartx.data.events.BorderCrossing;
import smartx.data.events.Dtc;
import smartx.data.events.EngineStatus;
import smartx.data.events.EventType;
import smartx.data.events.FuelConsumption;
import smartx.data.events.HarshAcceleration;
import smartx.data.events.HarshBreak;
import smartx.data.events.OverRpm;
import smartx.data.events.OverSpeed;
import smartx.data.events.TireCondition;
import smartx.data.events.VehicleStatus;
import smartx.data.events.VehicleStatusService;
import smartx.data.events.VehicleTracking;
import smartx.data.user.User;
import smartx.data.user.UserService;
import smartx.data.vehicle.Vehicle;
import smartx.data.vehicle.VehicleService;
import smartx.ui.forms.TableFormBorderCrossing;
import smartx.ui.forms.TableFormDtc;
import smartx.ui.forms.TableFormEngineStatus;
import smartx.ui.forms.TableFormFuelHarsh;
import smartx.ui.forms.TableFormMostRecentEvents;
import smartx.ui.forms.TableFormOver;
import smartx.ui.forms.TableFormTireCondition;
import smartx.ui.forms.TableFormVehicleTracking;
import smartx.ui.forms.TableFormVehicles;
import smartx.ui.forms.VehicleTrackingFilter;
import smartx.utils.Status;

@Controller
@ComponentScan
@SessionAttributes({"editedVehicle", "editedVehicleConfiguration"}) // DO NOT DELETE - Used for editing vehicle details
public class VehicleController {

	@Autowired
	private GlobalController globalController;
	@Autowired
	UserService userService;
	@Autowired
	private VehicleService vehicleService;
	@Autowired
	private DriverService driverService;
	@Autowired
	private VehicleConfigurationService vehicleConfigurationService;
	@Autowired
	private DeviceConfigurationService deviceConfigurationService;
	@Autowired
	private VehicleStatusService vehicleStatusService;
	@Autowired
	private smartx.data.events.BorderCrossingService borderCrossingService;
	@Autowired
	private smartx.data.events.DtcService dtcService;
	@Autowired
	private smartx.data.events.EngineStatusService engineStatusService;
	@Autowired
	private smartx.data.events.FuelConsumptionService fuelConsumptionService;
	@Autowired
	private smartx.data.events.HarshAccelerationService harshAccelerationService;
	@Autowired
	private smartx.data.events.HarshBreakService harshBreakService;
	@Autowired
	private smartx.data.events.OverRpmService overRpmService;
	@Autowired
	private smartx.data.events.OverSpeedService overSpeedService;
	@Autowired
	private smartx.data.events.TireConditionService tireConditionService;
	@Autowired
	private smartx.data.events.VehicleTrackingService vehicleTrackingService;
	@Autowired
	private DeviceCommunicationController deviceCommunicationController;

	private Vehicle newlyAddedVehicle;

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	// Since these 2 model attributes are declared as session attributes, when the session expires (if being left inactive 
	// for a certain amount of time), the objects also expire, so they need to be reinitialized
	@ModelAttribute("editedVehicle")
	public Vehicle populateEditedVehicle() {
		System.out.println("Session expired, reinitializing session attribute editedVehicle!");   
		return new Vehicle(); // populates editedVehicle for the first time if its null
	}
	@ModelAttribute("editedVehicleConfiguration")
	public VehicleConfiguration populateEditedVehicleConfiguration() {
		System.out.println("Session expired, reinitializing session attribute editedVehicleConfiguration!");    
		return new VehicleConfiguration(); // populates editedVehicleConfiguration for the first time if its null
	}

	// These 2 model attributes are used so that we are able to access the User record from db inside the topbar, in order to show the 
	// user's picture, username and e-mail
	@ModelAttribute("loggedInUser")
	public User getLoggedInUser() {
		User sessionUser = globalController.getLoginUser();
		User loggedInUser = null;
		if(sessionUser != null)
		{
			loggedInUser  = userService.getUserById(sessionUser.getId());
		}
		return loggedInUser;
	}
	@ModelAttribute("loggedInUserImage")
	public String getLoggedInUserPicture() {
		User sessionUser = globalController.getLoginUser();
		User loggedInUser = null;
		String zUserImage = null;
		if(sessionUser != null)
		{
			loggedInUser  = userService.getUserById(sessionUser.getId());
			if (loggedInUser.getPicture() != null) 
			{
				zUserImage = Base64.getEncoder().encodeToString(loggedInUser.getPicture());
			}
		}
		return zUserImage;
	}

	@RequestMapping("/vehicles")
	public String handleAllVehicles(Model model) {
		System.out.println("/vehicles GET request");
		// The getLoginUser method should only be used for retrieving the id of the logged in user, not the user entity, because the User record used inside
		// GlobalController is not always up-to-date
		List<Vehicle> vehiclesList = vehicleService.getVehiclesByUserId(globalController.getLoginUser().getId());
		List<TableFormVehicles> formList = new ArrayList<TableFormVehicles>();
		for (int i = 0; i < vehiclesList.size(); i++) {
			Vehicle currentVehicle = vehiclesList.get(i);
			DeviceConfiguration latestDeviceConfiguration = deviceConfigurationService
					.getMostRecentDeviceConfigurationByVehicleId(currentVehicle.getId());
			TableFormVehicles form = new TableFormVehicles();
			form.populateTableFormVehicles(currentVehicle, latestDeviceConfiguration);
			formList.add(form);
		}

		model.addAttribute("tableFormList", formList);
		return "manage_vehicles";
	}

	@RequestMapping("/vehicle/add")
	public String handleAddNewDevice(Model model) {
		System.out.println("/vehicle/add GET request");

		// get the list of all the user's unassigned drivers
		// The getLoginUser method should only be used for retrieving the id of the logged in user, not the user entity, because the User record used inside
		// GlobalController is not always up-to-date
		List<Driver> unassignedDrivers = driverService.getUnassignedDriversByUserId(globalController.getLoginUser().getId());
		int selectedDriverId = 0;

		model.addAttribute("newVehicle", new Vehicle());
		model.addAttribute("unassignedDrivers", unassignedDrivers);
		model.addAttribute("selectedDriverId", selectedDriverId);
		return "add_vehicle";
	}

	@RequestMapping(value = { "/vehicle/add" }, method = RequestMethod.POST)
	public String addNewDevice(Model model, @ModelAttribute("newVehicle") Vehicle newVehicle, 
			@ModelAttribute("selectedDriverId") int selectedDriverId, @RequestParam("vehicleImage") MultipartFile file,
			final RedirectAttributes redirectAttributes) {
		System.out.println("/vehicle/add POST request");

		// check that there is no other vehicle with the same device serial number in
		// the database
		if (vehicleService.getVehicleBySerialNumber(newVehicle.getDeviceSerialNumber()) == null) {

			// send success attribute
			redirectAttributes.addFlashAttribute("saveVehicle", "success");

			// add a clean vehicle configuration attribute to be updated
			model.addAttribute("newVehicleConfiguration", new VehicleConfiguration());
			// set the data introduced by the user to the newlyAddedVehicle, in order for it
			// to be accessible after the configuration is provided
			newlyAddedVehicle = new Vehicle();
			newlyAddedVehicle = newVehicle;

			// add additional configuration
			// add picture
			if ((file.getOriginalFilename() != null) && (!file.getOriginalFilename().isEmpty()) && (!file.isEmpty()))
			{
				System.out.println("Uploaded file name: " + file.getOriginalFilename());
				try 
				{
					byte[] fileContents = file.getBytes();
					newlyAddedVehicle.setPicture(fileContents);
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			} 
			else 
			{
				System.out.println("No file has been uploaded - default image will be displayed further.");
			}

			// LocalDateTime.now() uses the system default (RO) zone ID, but when converting to unix timestamp, the system default (RO) zone is
			// taken into account once again, so the offset is annulled => UTC unix timestamp
			LocalDateTime localDateTime = LocalDateTime.now();
			final long currentUnixTimestamp = localDateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
			newlyAddedVehicle.setCreationTimestamp(currentUnixTimestamp);
			newlyAddedVehicle.setLastReportedTimestamp(currentUnixTimestamp);
			// The getLoginUser method should only be used for retrieving the id of the logged in user, not the user entity, because the User record used inside
			// GlobalController is not always up-to-date
			int currentUserId = globalController.getLoginUser().getId();
			newlyAddedVehicle.setUser(userService.getUserById(currentUserId));
			if(selectedDriverId != 0)
			{
				// if the user selected a driver, update the newlyAddedVehicle with the specific driver
				Driver assignedDriver = driverService.getDriverById(selectedDriverId);
				newlyAddedVehicle.setDriver(assignedDriver);
			}
			else
			{
				// if the user did not select a driver, set the vehicle's driver to null
				newlyAddedVehicle.setDriver(null);
			}

			return "configuration_parameters";
		} else {
			System.out.println("A vehicle with the same device serial number already exists !!!");
			// if a vehicle with the same device serial number already exists, notify the
			// user and redirect him to the same page empty
			// send serial-number-exists attribute to 
			redirectAttributes.addFlashAttribute("saveVehicle", "serial-number-exists");
			return "redirect:/vehicle/add";
		}
	}

	@RequestMapping(value = { "/vehicle/add/configuration" }, method = RequestMethod.POST)
	public String addNewVehicleConfiguration(
			@ModelAttribute("newVehicleConfiguration") VehicleConfiguration newVehicleConfiguration,
			final RedirectAttributes redirectAttributes) {
		System.out.println("/vehicle/add/configuration POST request");

		// save the new vehicle
		vehicleService.addVehicle(newlyAddedVehicle);
		Driver assignedDriver = newlyAddedVehicle.getDriver();
		if(assignedDriver != null)
		{
			// we also must keep the driver records up-to-date, so the vehicle should also be set on the driver entity
			assignedDriver.setVehicle(newlyAddedVehicle);
			driverService.updateDriver(assignedDriver);
		}

		// save the new vehicle configuration
		newVehicleConfiguration.setVehicle(newlyAddedVehicle);
		// LocalDateTime.now() uses the system default (RO) zone ID, but when converting to unix timestamp, the system default (RO) zone is
		// taken into account once again, so the offset is annulled => UTC unix timestamp
		LocalDateTime localDateTime = LocalDateTime.now();
		final long currentUnixTimestamp = localDateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
		newVehicleConfiguration.setTimestamp(currentUnixTimestamp);
		vehicleConfigurationService.addVehicleConfiguration(newVehicleConfiguration);

		redirectAttributes.addFlashAttribute("msg", "success");

		return "redirect:/home";
	}

	@RequestMapping(value = "/vehicle/{id}")
	public String handleViewDevice(Model model, @PathVariable("id") int id, 
			@RequestParam(value="requestEvents", required=false) boolean requestEvents, 
			@RequestParam(value="requestLogs", required=false) boolean requestLogs) {
		System.out.println("/vehicle/" + id + " GET request");
		System.out.println("requestEvents is: " + requestEvents + ", requestLogs is: " + requestLogs);

		// get vehicle, device configuration, vehicle configuration and assigned driver from db
		Vehicle vehicle = vehicleService.getVehicleById(id);
		DeviceConfiguration lastDeviceConfiguration = deviceConfigurationService
				.getMostRecentDeviceConfigurationByVehicleId(id);
		VehicleConfiguration lastVehicleConfiguration = vehicleConfigurationService
				.getMostRecentVehicleConfigurationByVehicleId(id);
		Driver assignedDriver = vehicle.getDriver();
		// if the device has not been registered yet (the device initial message was not
		// received yet), there will be no device configuration saved
		if (lastDeviceConfiguration == null) {
			lastDeviceConfiguration = new DeviceConfiguration();
		}
		System.out.println("Viewing device with serial number: " + vehicle.getDeviceSerialNumber());

		if(requestEvents)
		{
			System.out.println("REQUEST EVENTS was selected!");
			// create and send portal request vehicle status message in order to get ALL events
			deviceCommunicationController.triggerSendingPortalRequestVehicleStatusMessage(vehicle.getDeviceSerialNumber(), true);
		}
		if(requestLogs)
		{
			System.out.println("REQUEST LOGS was selected");
			vehicle.setExportLogsInProgress(true);
			vehicleService.updateVehicle(vehicle);

			// create and send portal extract logs message
			deviceCommunicationController.triggerSendingPortalExtractLogsMessage(vehicle.getDeviceSerialNumber());
		}

		// depending on the vehicle's status, the View Vehicle Tracking, Request Events and Request Extract Logs
		// buttons should be enabled/disabled
		boolean enableViewButtons = false;
		boolean enableRequestButtons = false;
		String vehicleStatus = vehicle.getStatus();
		switch (vehicleStatus) {
		case "AVAILABLE":
			enableViewButtons = true;
			enableRequestButtons = true;
			break;
		case "UNAVAILABLE":
		case "CONFIGURATION PENDING":
		case "VIN MISMATCH":
			enableViewButtons = true;
			break;
		case "NOT REGISTERED":
			break;
		default:
			System.out.println("Invalid vehicle status: " + vehicleStatus);
		}

		VehicleStatus mostRecentVehicleStatusEvent = vehicleStatusService.getMostRecentVehicleStatusByVehicleId(id);

		// Initialize the events list
		List<TableFormMostRecentEvents> mostRecentEventsList = new ArrayList<TableFormMostRecentEvents>();

		// get the most recent event of each type, construct TableFormMostRecentEvents from it and append it to the list
		mostRecentEventsList.addAll(vehicleService.getMostRecentsEventDetailsTablesForVehicle(id, vehicle.getVehicleNumber()));

		// sort the list
		Collections.sort(mostRecentEventsList, Collections.reverseOrder(Comparator.comparingLong(TableFormMostRecentEvents::getTimestamp)));

		System.out.println("Total number of events from this vehicles: " + mostRecentEventsList.size());

		// add the vehicle image / null if the user did not upload one
		if (vehicle.getPicture() != null) 
		{
			String zVehicleImage = Base64.getEncoder().encodeToString(vehicle.getPicture());

			model.addAttribute("vehicleImage", zVehicleImage);
		} 
		else 
		{
			model.addAttribute("vehicleImage", null);
		}
		
		// send URL to connect when pressing View Logs Archive
		String logsUrl = "http://x.opentransport.ro/uploads/";
		model.addAttribute("url", logsUrl);

		model.addAttribute("viewedVehicle", vehicle);
		model.addAttribute("newestDeviceConfiguration", lastDeviceConfiguration);
		model.addAttribute("newestVehicleConfiguration", lastVehicleConfiguration);
		model.addAttribute("assignedDriver", assignedDriver);
		model.addAttribute("displayViewButtons", enableViewButtons);
		model.addAttribute("displayRequestButtons", enableRequestButtons);
		model.addAttribute("mostRecentVehicleStatusEvent", mostRecentVehicleStatusEvent);
		model.addAttribute("mostRecentEventsList", mostRecentEventsList);

		return "vehicle_details";
	}

	@RequestMapping("/vehicle/edit/{id}")
	public String handleEditDevice(Model model, @PathVariable("id") int id) {
		System.out.println("/vehicle/edit/" + id + " GET request");

		// get vehicle and vehicle configuration from db
		Vehicle vehicle = vehicleService.getVehicleById(id);
		VehicleConfiguration lastVehicleConfiguration = vehicleConfigurationService
				.getMostRecentVehicleConfigurationByVehicleId(id);
		// get the list of all the user's unassigned drivers
		// The getLoginUser method should only be used for retrieving the id of the logged in user, not the user entity, because the User record used inside
		// GlobalController is not always up-to-date
		List<Driver> unassignedDrivers = driverService.getUnassignedDriversByUserId(globalController.getLoginUser().getId());
		Driver assignedDriver = vehicle.getDriver();
		int selectedDriverId = 0;
		if(assignedDriver != null)
		{
			System.out.println("Vehicle has an assigned driver with id: " + assignedDriver.getId());
			selectedDriverId = assignedDriver.getId();
			// if there already is an assigned driver, add him to the list in order to display his username at the beginning of the drop-down list
			unassignedDrivers.add(assignedDriver);
		}

		System.out.println("Editing device with serial number: " + vehicle.getDeviceSerialNumber());
		System.out.println("selectedDriverId: " + selectedDriverId);

		// add the vehicle image / null if the user did not upload one
		if (vehicle.getPicture() != null) 
		{
			String zVehicleImage = Base64.getEncoder().encodeToString(vehicle.getPicture());

			model.addAttribute("vehicleImage", zVehicleImage);
		} 
		else 
		{
			model.addAttribute("vehicleImage", null);
		}

		model.addAttribute("editedVehicle", vehicle);
		model.addAttribute("editedVehicleConfiguration", lastVehicleConfiguration);
		model.addAttribute("unassignedDrivers", unassignedDrivers);
		model.addAttribute("assignedDriver", assignedDriver);
		model.addAttribute("selectedDriverId", selectedDriverId);
		return "edit_vehicle";
	}

	@RequestMapping(value = {"/vehicle/edit/{id}"}, method = RequestMethod.POST)
	public String editDevice(Model model,@PathVariable("id") int id, @ModelAttribute("editedVehicle") Vehicle editedVehicle, 
			@ModelAttribute("editedVehicleConfiguration") VehicleConfiguration editedVehicleConfiguration, 
			@ModelAttribute("selectedDriverId") int selectedDriverId, @RequestParam("vehicleImage") MultipartFile file, 
			SessionStatus sessionStatus,
			final RedirectAttributes redirectAttributes) {
		System.out.println("/vehicle/edit/" + id + " POST request");

		// get initial vehicle record and latest vehicle configuration record from db
		Vehicle portalVehicle = vehicleService.getVehicleById(id);
		VehicleConfiguration latestVehicleConfiguration = vehicleConfigurationService.getMostRecentVehicleConfigurationByVehicleId(id);

		// 1) CHECK VEHICLE DETAILS

		// if the vehicle number/model/colour/vin was changed, the current vehicle from db should be updated
		// in case of vin, the user is allowed (via edit_vehicle.html) to update its value only if the vehicle status is not registered 
		// or vin mismatch. If the vehicle status is vin mismatch and the vin value is edited, a Portal Configuration message 
		// should be sent and the vehicle status should be changed to CONFIGURATION PENDING until receiving the 
		// Device Configuration Finished Message
		boolean shouldUpdateVehicle = false;
		boolean shouldSendPortalMessage = false;

		if(!portalVehicle.getVehicleNumber().equals(editedVehicle.getVehicleNumber()))
		{
			portalVehicle.setVehicleNumber(editedVehicle.getVehicleNumber());
			shouldUpdateVehicle = true;
		}
		if(!portalVehicle.getVehicleModel().equals(editedVehicle.getVehicleModel()))
		{
			portalVehicle.setVehicleModel(editedVehicle.getVehicleModel());
			shouldUpdateVehicle = true;
		}
		if(!portalVehicle.getVehicleColour().equals(editedVehicle.getVehicleColour()))
		{
			portalVehicle.setVehicleColour(editedVehicle.getVehicleColour());
			shouldUpdateVehicle = true;
		}
		// check if vin has been edited (allowed only if the vehicle status is NOT REGISTERED or CONFIGURATION PENDING)
		if(!portalVehicle.getVin().equals(editedVehicle.getVin()))
		{
			portalVehicle.setVin(editedVehicle.getVin());
			shouldUpdateVehicle = true;

			// if the vehicle status is vin mismatch
			if(portalVehicle.getStatus().equals(Status.VIN_MISMATCH.getValue()))
			{
				System.out.println("VIN edited and current status is VIN MISMATCH => change status to CONFIGURATION PENDING & "
						+ "send Portal Configuration Message!");
				// change vehicle status to CONFIGURATION PENDING
				portalVehicle.setStatus(Status.CONFIGURATION_PENDING.getValue());

				// set flag to true
				shouldSendPortalMessage = true;
			}
		}

		// check if a different picture was uploaded
		if ((file.getOriginalFilename() != null) && (!file.getOriginalFilename().isEmpty()) && (!file.isEmpty())) 
		{
			System.out.println("Uploaded file name: " + file.getOriginalFilename());
			try 
			{
				byte[] fileContents = file.getBytes();

				portalVehicle.setPicture(fileContents);

				shouldUpdateVehicle = true;

			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		} 
		else 
		{
			System.out.println("No file has been uploaded - keep the last image.");
		}

		// check if a different driver was assigned
		// the assignedDriver will be null when selectedDriverId = 0
		// this means that the user selected the No driver option
		Driver assignedDriver = null;
		if(selectedDriverId != 0)
		{
			assignedDriver = driverService.getDriverById(selectedDriverId);
		}
		if(portalVehicle.getDriver() == null)
		{
			if(assignedDriver != null)
			{
				System.out.println("Driver was assigned during edit, update assigned driver for vehicle!");
				portalVehicle.setDriver(assignedDriver);

				// we also must keep the driver records up-to-date, so the vehicle should also be set on the driver entity
				assignedDriver.setVehicle(portalVehicle);
				driverService.updateDriver(assignedDriver);

				shouldUpdateVehicle = true;

				System.out.println("Driver id: " + assignedDriver.getId());
				System.out.println("Driver username: " + assignedDriver.getDriverUsername());
				System.out.println("Driver fullname: " + assignedDriver.getFullName());
				System.out.println("Driver license: " + assignedDriver.getLicenseNumber());
				System.out.println("Driver phone: " + assignedDriver.getPhoneNumber());
				System.out.println("Driver address: " + assignedDriver.getAddress());
				System.out.println("Driver vehicle id: " + assignedDriver.getVehicle().getId());
			}
			else
			{
				System.out.println("No driver was assigned for vehicle!");
				// nothing to do here
			}
		}
		else
		{
			if(assignedDriver == null)
			{
				System.out.println("The vehicle has no assigned driver anymore!");

				// we also must keep the driver records up-to-date, so before setting the vehicle's driver to null, 
				// the previous driver's vehicle should also be set to null
				Driver previousDriver = portalVehicle.getDriver();
				previousDriver.setVehicle(null);
				driverService.updateDriver(previousDriver);

				portalVehicle.setDriver(null);

				shouldUpdateVehicle = true;
			}
			else
			{
				if(portalVehicle.getDriver().getId() == assignedDriver.getId())
				{
					System.out.println("Assigned driver was not changed for vehicle!");
					// nothing to do here
				}
				else
				{
					System.out.println("Assigned driver changed for vehicle!!");

					// we also must keep the driver records up-to-date, so before setting the vehicle's driver to the new one received,
					// the previous driver's vehicle should be set to null
					Driver previousDriver = portalVehicle.getDriver();
					previousDriver.setVehicle(null);
					driverService.updateDriver(previousDriver);

					// we also must keep the driver records up-to-date, so the vehicle should also be set on the current driver entity
					assignedDriver.setVehicle(portalVehicle);
					driverService.updateDriver(assignedDriver);

					portalVehicle.setDriver(assignedDriver);
					shouldUpdateVehicle = true;

					System.out.println("Driver id: " + assignedDriver.getId());
					System.out.println("Driver username: " + assignedDriver.getDriverUsername());
					System.out.println("Driver fullname: " + assignedDriver.getFullName());
					System.out.println("Driver license: " + assignedDriver.getLicenseNumber());
					System.out.println("Driver phone: " + assignedDriver.getPhoneNumber());
					System.out.println("Driver address: " + assignedDriver.getAddress());
				}
			}
		}

		if(shouldUpdateVehicle)
		{
			System.out.println("Some vehicle characteristics were edited, update current vehicle record!");
			vehicleService.updateVehicle(portalVehicle);
		}

		// 2) CHECK CONFIGURATION PARAMETERS

		boolean configurationParametersUnchanged = latestVehicleConfiguration.hasTheSameParameterConfiguration(editedVehicleConfiguration);
		boolean descriptionUnchanged = latestVehicleConfiguration.hasTheSameDescription(editedVehicleConfiguration);

		// if the user changed any parameter or description
		if((!configurationParametersUnchanged) || (!descriptionUnchanged))
		{
			// if one of the configuration parameters (besides the description) was changed, we need to check the vehicle status 
			// to decide on the appropriate action
			if(!configurationParametersUnchanged)
			{
				String vehicleStatus = portalVehicle.getStatus();
				System.out.println("Configuration parameters were changed and current status is: " + vehicleStatus);
				switch (vehicleStatus) {
				case "NOT REGISTERED":
					// the user is allowed to update parameters as he pleases, no message for device should be sent
					break;
				case "CONFIGURATION PENDING":
				case "VIN MISMATCH":
					// the user is NOT allowed to update parameters
					break;
				case "UNAVAILABLE":
				case "AVAILABLE":
					// should send Portal Configuration message
					shouldSendPortalMessage = true;
					// change vehicle status to CONFIGURATION PENDING
					portalVehicle.setStatus(Status.CONFIGURATION_PENDING.getValue());
					vehicleService.updateVehicle(portalVehicle);
					break;
				default:
					System.out.println("Invalid vehicle status: " + vehicleStatus);
				}
			}
			else
			{
				// only the description changed => nothing else should be done
				System.out.println("Only description was changed");
			}

			// since the user modified the vehicle configuration, save a new one
			System.out.println("Add a new configuration!");
			VehicleConfiguration newVehicleConfiguration = new VehicleConfiguration();
			newVehicleConfiguration.setDtc(editedVehicleConfiguration.getDtc());
			newVehicleConfiguration.setFuelConsumption(editedVehicleConfiguration.getFuelConsumption());
			newVehicleConfiguration.setHarshAcceleration(editedVehicleConfiguration.getHarshAcceleration());
			newVehicleConfiguration.setHarshBreak(editedVehicleConfiguration.getHarshBreak());
			newVehicleConfiguration.setOverRpm(editedVehicleConfiguration.getOverRpm());
			newVehicleConfiguration.setOverSpeed(editedVehicleConfiguration.getOverSpeed());
			newVehicleConfiguration.setTireCondition(editedVehicleConfiguration.getTireCondition());
			newVehicleConfiguration.setDescription(editedVehicleConfiguration.getDescription());
			newVehicleConfiguration.setOnlineFrequencyInterval(editedVehicleConfiguration.getOnlineFrequencyInterval());
			// LocalDateTime.now() uses the system default (RO) zone ID, but when converting to unix timestamp, the system default (RO) zone is
			// taken into account once again, so the offset is annulled => UTC unix timestamp
			LocalDateTime localDateTime = LocalDateTime.now();
			final long currentUnixTimestamp = localDateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
			newVehicleConfiguration.setTimestamp(currentUnixTimestamp);
			newVehicleConfiguration.setVehicle(portalVehicle);
			vehicleConfigurationService.addVehicleConfiguration(newVehicleConfiguration);
		}

		if(shouldSendPortalMessage)
		{
			// create portal configuration message and send
			deviceCommunicationController.triggerSendingPortalConfigurationMessage(portalVehicle.getDeviceSerialNumber());
		}

		sessionStatus.setComplete();

		return "redirect:/vehicle/" + id + "?requestEvents=false&requestLogs=false";
	}

	@RequestMapping("/vehicle/delete/{id}")
	public String deleteDevice(Model model, @PathVariable("id") int id) {
		System.out.println("/vehicle/delete/" + id + " GET request");

		// get vehicle from db
		Vehicle vehicle = vehicleService.getVehicleById(id);
		System.out.println("Deleting device with serial number: " + vehicle.getDeviceSerialNumber());

		// This method is called only after the user has been warned that he will loose all the device information
		// if he proceeds (done inside manage_vehicles.html) and has decided to proceed
		vehicleService.deleteVehicle(id);

		return "redirect:/vehicles";
	}

	@GetMapping("/vehicle/tracking/{id}")
	public String handleVehicleTracking(Model model, @PathVariable("id") int vehicleId) {
		System.out.println("/vehicle/tracking/" + vehicleId + " GET request");

		// Get the current date in UTC.
		// LocalDateTime.now() uses the system default (RO) zone ID, so we must specify to use GMT zone ID
		LocalDate localDate = LocalDate.now(TimeZone.getTimeZone("GMT").toZoneId());

		List<VehicleTracking> listOfVehicleTrackings = new ArrayList<VehicleTracking>();
		listOfVehicleTrackings = vehicleTrackingService.getVehicleTrackingsByVehicleIdAndTimestamp(vehicleId,
				localDate);
		// create table form list to properly display the events for the current day
		List<TableFormVehicleTracking> vehicleTrackingTableFormList = 
				vehicleTrackingService.getVehicleTrackingTableForms(listOfVehicleTrackings);

		List<GMapsCoordinates> listOfCoordinates = new ArrayList<>();
		listOfCoordinates = vehicleTrackingService
				.getGoogleMapsCoordinatesFroVehicleTrackingEvents(listOfVehicleTrackings);

		// Get the date in a specific format.
		// IMPORTANT: If the format is not used, the date will not be displayed on the
		// frontend.
		String zFormatedDate = localDate.format(formatter);

		// Get the first date of the current month.
		LocalDate currentDateMinusAMonth = localDate.minus(Period.ofDays(30));
		String zFormattedFirstDate = currentDateMinusAMonth.format(formatter);

		VehicleTrackingFilter newVehicleTrackingFilter = new VehicleTrackingFilter();

		model.addAttribute("vehicleTrackingTableFormList", vehicleTrackingTableFormList);
		model.addAttribute("listOfCoordinates", listOfCoordinates);
		model.addAttribute("newFilter", newVehicleTrackingFilter);
		model.addAttribute("currentDate", zFormatedDate);
		model.addAttribute("vehicleId", vehicleId);
		model.addAttribute("minDate", zFormattedFirstDate);
		model.addAttribute("maxDate", zFormatedDate);

		return "vehicle_tracking";
	}

	@PostMapping("/vehicle/tracking/{id}")
	public String handleVehicleTrackingByDate(Model model,
			@ModelAttribute("newFilter") VehicleTrackingFilter newVehicleTrackingFilter,
			final RedirectAttributes redirectAttributes, @PathVariable("id") int vehicleId) {

		System.out.println("/vehicle/tracking/" + vehicleId + " POST request");

		System.out.println("Process vehicle tracking for date: " + newVehicleTrackingFilter.getDate());

		List<VehicleTracking> listOfVehicleTrackings = new ArrayList<VehicleTracking>();
		listOfVehicleTrackings = vehicleTrackingService.getVehicleTrackingsByVehicleIdAndTimestamp(vehicleId,
				newVehicleTrackingFilter.getDate());
		// create table form list to properly display the events
		List<TableFormVehicleTracking> vehicleTrackingTableFormList = 
				vehicleTrackingService.getVehicleTrackingTableForms(listOfVehicleTrackings);

		List<GMapsCoordinates> listOfCoordinates = new ArrayList<>();
		listOfCoordinates = vehicleTrackingService
				.getGoogleMapsCoordinatesFroVehicleTrackingEvents(listOfVehicleTrackings);

		// Get the current date to be as max date in calendar.
		// LocalDateTime.now() uses the system default (RO) zone ID, so we must specify to use GMT zone ID
		LocalDate curentDate = LocalDate.now(TimeZone.getTimeZone("GMT").toZoneId());
		String zFormatedCurrentDate = curentDate.format(formatter);

		LocalDate filteredDate = newVehicleTrackingFilter.getDate();
		// Get the date in a specific format.
		// IMPORTANT: If the format is not used, the date will not be displayed on the
		// frontend.
		String zFormatedDate = filteredDate.format(formatter);

		// Get the first date of the current month.
		LocalDate currentDateMinusAMonth = filteredDate.minus(Period.ofDays(30));
		String zFormattedFirstDate = currentDateMinusAMonth.format(formatter);

		model.addAttribute("vehicleTrackingTableFormList", vehicleTrackingTableFormList);
		model.addAttribute("listOfCoordinates", listOfCoordinates);
		model.addAttribute("currentDate", zFormatedDate);
		model.addAttribute("minDate", zFormattedFirstDate);
		model.addAttribute("maxDate", zFormatedCurrentDate);
		model.addAttribute("vehicleId", vehicleId);
		return "vehicle_tracking";
	}

	@RequestMapping(value = "/vehicle/{id}/event/{eventValue}")
	public String handleViewEvents(Model model, @PathVariable("id") int id, @PathVariable("eventValue") int eventValue) {
		System.out.println("/vehicle/" + id + "/event/" + eventValue + " GET request");
		System.out.println("Requested event type value: " + eventValue);

		model.addAttribute("vehicleId", id);

		if(eventValue == EventType.BORDER_CROSSING.getValue())
		{
			String eventType = EventType.BORDER_CROSSING.getName();
			model.addAttribute("eventType", eventType);
			
			List<BorderCrossing> allVehiclesBorderCrossings = borderCrossingService.getAllBorderCrossingsOrderedDescendingByVehicleId(id);
			// create table form list to properly display the events
			List<TableFormBorderCrossing> borderCrossingTableFormList = 
					borderCrossingService.getBorderCrossingTableForms(allVehiclesBorderCrossings);
			model.addAttribute("borderCrossingTableFormList", borderCrossingTableFormList);
			return "events_border_crossing";
		}
		else if(eventValue == EventType.DTC.getValue())
		{
			String eventType = EventType.DTC.getName();
			model.addAttribute("eventType", eventType);
			
			List<Dtc> allVehiclesDtcs = dtcService.getAllDtcsOrderedDescendingByVehicleId(id);
			// create table form list to properly display the events
			List<TableFormDtc> dtcTableFormList = 
					dtcService.getDtcTableForms(allVehiclesDtcs);
			model.addAttribute("dtcTableFormList", dtcTableFormList);
			return "events_dtc";
		}
		else if(eventValue == EventType.ENGINE_STATUS.getValue())
		{
			String eventType = EventType.ENGINE_STATUS.getName();
			model.addAttribute("eventType", eventType);
			
			List<EngineStatus> allVehiclesEngineStatuses = engineStatusService.getAllEngineStatusesOrderedDescendingByVehicleId(id);
			// create table form list to properly display the events
			List<TableFormEngineStatus> engineStatusTableFormList = 
					engineStatusService.getEngineStatusTableForms(allVehiclesEngineStatuses);
			model.addAttribute("engineStatusTableFormList", engineStatusTableFormList);
			return "events_engine_status";
		}
		else if(eventValue == EventType.FUEL_CONSUMPTION.getValue())
		{
			String eventType = EventType.FUEL_CONSUMPTION.getName();
			model.addAttribute("eventType", eventType);
			
			List<FuelConsumption> allVehiclesFuelConsumptions = fuelConsumptionService.getAllFuelConsumptionsOrderedDescendingByVehicleId(id);
			// create table form list to properly display the events
			List<TableFormFuelHarsh> fuelConsumptionTableFormList = 
					fuelConsumptionService.getFuelConsumptionTableForms(allVehiclesFuelConsumptions);
			model.addAttribute("fuelHarshTableFormList", fuelConsumptionTableFormList);
			return "events_fuel_harsh";
		}
		else if(eventValue == EventType.HARSH_ACCELERATION.getValue())
		{
			String eventType = EventType.HARSH_ACCELERATION.getName();
			model.addAttribute("eventType", eventType);
			
			List<HarshAcceleration> allVehiclesHarshAccelerations = harshAccelerationService.getAllHarshAccelerationsOrderedDescendingByVehicleId(id);
			// create table form list to properly display the events
			List<TableFormFuelHarsh> harshAccelerationTableFormList = 
					harshAccelerationService.getHarshAccelerationTableForms(allVehiclesHarshAccelerations);
			model.addAttribute("fuelHarshTableFormList", harshAccelerationTableFormList);
			return "events_fuel_harsh";
		}
		else if(eventValue == EventType.HARSH_BREAK.getValue())
		{
			String eventType = EventType.HARSH_BREAK.getName();
			model.addAttribute("eventType", eventType);
			
			List<HarshBreak> allVehiclesHarshBreaks = harshBreakService.getAllHarshBreaksOrderedDescendingByVehicleId(id);
			// create table form list to properly display the events
			List<TableFormFuelHarsh> harshBreakTableFormList = 
					harshBreakService.getHarshBreakTableForms(allVehiclesHarshBreaks);
			model.addAttribute("fuelHarshTableFormList", harshBreakTableFormList);
			return "events_fuel_harsh";
		}
		else if(eventValue == EventType.OVER_RPM.getValue()) 
		{
			String eventType = EventType.OVER_RPM.getName();
			model.addAttribute("eventType", eventType);
			
			List<OverRpm> allVehiclesOverRpms = overRpmService.getAllOverRpmsOrderedDescendingByVehicleId(id);
			// create table form list to properly display the events
			List<TableFormOver> overRpmTableFormList = 
					overRpmService.getOverRpmTableForms(allVehiclesOverRpms);
			model.addAttribute("overTableFormList", overRpmTableFormList);
			return "events_over";
		}
		else if(eventValue == EventType.OVER_SPEED.getValue())
		{
			String eventType = EventType.OVER_SPEED.getName();
			model.addAttribute("eventType", eventType);
			
			List<OverSpeed> allVehiclesOverSpeeds = overSpeedService.getAllOverSpeedsOrderedDescendingByVehicleId(id);
			// create table form list to properly display the events
			List<TableFormOver> overSpeedTableFormList = 
					overSpeedService.getOverSpeedTableForms(allVehiclesOverSpeeds);
			model.addAttribute("overTableFormList", overSpeedTableFormList);
			return "events_over";
		}
		else if(eventValue == EventType.TIRE_CONDITION.getValue())
		{
			String eventType = EventType.TIRE_CONDITION.getName();
			model.addAttribute("eventType", eventType);
			
			List<TireCondition> allVehiclesTireConditions = tireConditionService.getAllTireConditionsOrderedDescendingByVehicleId(id);
			// create table form list to properly display the events
			List<TableFormTireCondition> tireConditionTableFormList = 
					tireConditionService.getTireConditionTableForms(allVehiclesTireConditions);
			model.addAttribute("tireConditionTableFormList", tireConditionTableFormList);
			return "events_tire_condition";
		}
		else
		{
			String eventType = EventType.VEHICLE_TRACKING.getName();
			model.addAttribute("eventType", eventType);
			
			List<VehicleTracking> allVehiclesVehicleTrackings = vehicleTrackingService.getAllVehicleTrackingsOrderedDescendingByVehicleId(id);
			// create table form list to properly display the events
			List<TableFormVehicleTracking> vehicleTrackingTableFormList = 
					vehicleTrackingService.getVehicleTrackingTableForms(allVehiclesVehicleTrackings);
			model.addAttribute("vehicleTrackingTableFormList", vehicleTrackingTableFormList);
			return "events_vehicle_tracking";
		}
	}

}
