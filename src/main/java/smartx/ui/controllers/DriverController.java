package smartx.ui.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import smartx.data.driver.Driver;
import smartx.data.driver.DriverService;
import smartx.data.user.User;
import smartx.data.user.UserService;
import smartx.data.vehicle.Vehicle;
import smartx.data.vehicle.VehicleService;
import smartx.ui.forms.TableFormDrivers;

@Controller
@ComponentScan
@SessionAttributes({"editedDriver"}) // DO NOT DELETE - Used for editing driver details
public class DriverController {

	@Autowired
	private GlobalController globalController;
	@Autowired
	UserService userService;
	@Autowired
	private VehicleService vehicleService;
	@Autowired
	private DriverService driverService;

	// Since this model attribute is declared as a session attribute, when the session expires (if being left inactive 
	// for a certain amount of time), the object also expires, so it needs to be reinitialized
	@ModelAttribute("editedDriver")
	public Driver populateEditedDriver() {
		System.out.println("Session expired, reinitializing session attribute editedDriver!");   
		return new Driver(); // populates editedDriver for the first time if its null
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

	@RequestMapping("/drivers")
	public String handleAllDrivers(Model model) {
		System.out.println("/drivers GET request");
		// The getLoginUser method should only be used for retrieving the id of the logged in user, not the user entity, because the User record used inside
		// GlobalController is not always up-to-date
		List<Driver> driversList = driverService.getDriversByUserId(globalController.getLoginUser().getId());
		List<TableFormDrivers> formList = new ArrayList<TableFormDrivers>();
		for (int i = 0; i < driversList.size(); i++) {
			Driver currentDriver = driversList.get(i);
			TableFormDrivers form = new TableFormDrivers();
			form.populateTableFormDrivers(currentDriver);
			formList.add(form);
		}

		model.addAttribute("tableFormList", formList);
		return "manage_drivers";
	}

	@RequestMapping("/driver/add")
	public String handleAddNewDriver(Model model) {
		System.out.println("/driver/add GET request");

		// get the list of all the user's unassigned vehicles
		// The getLoginUser method should only be used for retrieving the id of the logged in user, not the user entity, because the User record used inside
		// GlobalController is not always up-to-date
		List<Vehicle> unassignedVehicles = vehicleService.getUnassignedVehiclesByUserId(globalController.getLoginUser().getId());
		int selectedVehicleId = 0;

		model.addAttribute("newDriver", new Driver());
		model.addAttribute("unassignedVehicles", unassignedVehicles);
		model.addAttribute("selectedVehicleId", selectedVehicleId);
		return "add_driver";
	}

	@RequestMapping(value = { "/driver/add" }, method = RequestMethod.POST)
	public String addNewDriver(Model model, @ModelAttribute("newDriver") Driver newDriver, 
			@ModelAttribute("selectedVehicleId") int selectedVehicleId, @RequestParam("driverImage") MultipartFile file,
			final RedirectAttributes redirectAttributes) {
		System.out.println("/driver/add POST request");
		// check that there is no other driver for the currently logged in user with the same username in
		// the database
		// The getLoginUser method should only be used for retrieving the id of the logged in user, not the user entity, because the User record used inside
		// GlobalController is not always up-to-date
		if(!driverService.getDriversUsernamesByUserId(globalController.getLoginUser().getId()).contains(newDriver.getDriverUsername()))
		{
			// send success attribute
			redirectAttributes.addFlashAttribute("saveDriver", "success");

			// set the data introduced by the user to a new Driver record and store it in db
			Driver newlyAddedDriver = new Driver();
			newlyAddedDriver = newDriver;

			// add additional configuration
			// add picture
			if ((file.getOriginalFilename() != null) && (!file.getOriginalFilename().isEmpty()) && (!file.isEmpty()))
			{
				System.out.println("Uploaded file name: " + file.getOriginalFilename());
				try 
				{
					byte[] fileContents = file.getBytes();
					newlyAddedDriver.setPicture(fileContents);
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

			// The getLoginUser method should only be used for retrieving the id of the logged in user, not the user entity, because the User record used inside
			// GlobalController is not always up-to-date
			int currentUserId = globalController.getLoginUser().getId();
			newlyAddedDriver.setUser(userService.getUserById(currentUserId));
			if(selectedVehicleId != 0)
			{	
				// if the user selected a vehicle, update the newlyAddedDriver with the specific vehicle
				Vehicle assignedVehicle = vehicleService.getVehicleById(selectedVehicleId);
				newlyAddedDriver.setVehicle(assignedVehicle);
			}
			else
			{
				// if the user did not select a vehicle, set the driver's vehicle to null
				newlyAddedDriver.setVehicle(null);
			}

			// save the new driver
			driverService.addDriver(newlyAddedDriver);

			if(selectedVehicleId != 0)
			{
				Vehicle assignedVehicle = vehicleService.getVehicleById(selectedVehicleId);
				// we also must keep the vehicle records up-to-date, so the driver should also be set on the vehicle entity
				assignedVehicle.setDriver(newlyAddedDriver);
				vehicleService.updateVehicle(assignedVehicle);
			}

			return "redirect:/home";
		}
		else 
		{
			System.out.println("A driver with the same username was already added by the user !!!");
			// if a driver with the same username was already added by the user, notify the
			// user and redirect him to the same page empty
			// send username-exists attribute
			redirectAttributes.addFlashAttribute("saveDriver", "username-exists");
			return "redirect:/driver/add";
		}
	}

	@RequestMapping(value = "/driver/{id}")
	public String handleViewDriver(Model model, @PathVariable("id") int id) {
		System.out.println("/driver/" + id + " GET request");

		// get driver and assigned vehicle from db
		Driver driver = driverService.getDriverById(id);
		Vehicle assignedVehicle = driver.getVehicle();

		System.out.println("Viewing driver with username: " + driver.getDriverUsername());

		// add the driver image / null if the user did not upload one
		if (driver.getPicture() != null) 
		{
			String zDriverImage = Base64.getEncoder().encodeToString(driver.getPicture());

			model.addAttribute("driverImage", zDriverImage);
		} 
		else 
		{
			model.addAttribute("driverImage", null);
		}

		model.addAttribute("viewedDriver", driver);
		model.addAttribute("assignedVehicle", assignedVehicle);

		return "driver_details";
	}

	@RequestMapping("/driver/edit/{id}")
	public String handleEditDriver(Model model, @PathVariable("id") int id) {
		System.out.println("/driver/edit/" + id + " GET request");
		// get driver from db
		Driver driver = driverService.getDriverById(id);
		// get the list of all the user's unassigned vehicles
		// The getLoginUser method should only be used for retrieving the id of the logged in user, not the user entity, because the User record used inside
		// GlobalController is not always up-to-date
		List<Vehicle> unassignedVehicles = vehicleService.getUnassignedVehiclesByUserId(globalController.getLoginUser().getId());
		Vehicle assignedVehicle = driver.getVehicle();
		int selectedVehicleId = 0;
		if(assignedVehicle != null)
		{
			System.out.println("Driver has an assigned vehicle with id: " + assignedVehicle.getId());
			selectedVehicleId = assignedVehicle.getId();
			// if there already is an assigned vehicle, add it to the list in order to display its vehicle number at the beginning of the drop-down list
			unassignedVehicles.add(assignedVehicle);
		}

		System.out.println("Editing driver with username: " + driver.getDriverUsername());
		System.out.println("selectedVehicleId: " + selectedVehicleId);

		// add the driver image / null if the user did not upload one
		if (driver.getPicture() != null) 
		{
			String zDriverImage = Base64.getEncoder().encodeToString(driver.getPicture());

			model.addAttribute("driverImage", zDriverImage);
		} 
		else 
		{
			model.addAttribute("driverImage", null);
		}

		model.addAttribute("editedDriver", driver);
		model.addAttribute("unassignedVehicles", unassignedVehicles);
		model.addAttribute("assignedVehicle", assignedVehicle);
		model.addAttribute("selectedVehicleId", selectedVehicleId);

		return "edit_driver";
	}

	@RequestMapping(value = {"/driver/edit/{id}"}, method = RequestMethod.POST)
	public String editDriver(@PathVariable("id") int id, @ModelAttribute("editedDriver") Driver editedDriver, 
			@ModelAttribute("selectedVehicleId") int selectedVehicleId, @RequestParam("driverImage") MultipartFile file,
			SessionStatus sessionStatus,
			final RedirectAttributes redirectAttributes) {
		System.out.println("/driver/edit/" + id + " POST request");

		// get initial driver record from db
		Driver portalDriver = driverService.getDriverById(id);

		// 1) CHECK DRIVER DETAILS

		// if the driver's full name, license number, address or phone number was changed, the current driver from db should be updated
		// the user is not allowed to update the driver's username 
		boolean shouldUpdateDriver = false;

		if(!portalDriver.getFullName().equals(editedDriver.getFullName()))
		{
			portalDriver.setFullName(editedDriver.getFullName());
			shouldUpdateDriver = true;
		}
		if(!portalDriver.getLicenseNumber().equals(editedDriver.getLicenseNumber()))
		{
			portalDriver.setLicenseNumber(editedDriver.getLicenseNumber());
			shouldUpdateDriver = true;
		}
		if(!portalDriver.getAddress().equals(editedDriver.getAddress()))
		{
			portalDriver.setAddress(editedDriver.getAddress());
			shouldUpdateDriver = true;
		}
		if(!portalDriver.getPhoneNumber().equals(editedDriver.getPhoneNumber()))
		{
			portalDriver.setPhoneNumber(editedDriver.getPhoneNumber());
			shouldUpdateDriver = true;
		}

		// check if a different picture was uploaded
		if ((file.getOriginalFilename() != null) && (!file.getOriginalFilename().isEmpty()) && (!file.isEmpty())) 
		{
			System.out.println("Uploaded file name: " + file.getOriginalFilename());
			try 
			{
				byte[] fileContents = file.getBytes();

				portalDriver.setPicture(fileContents);

				shouldUpdateDriver = true;

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

		// check if a different vehicle was assigned
		// the assignedVehicle will be null when selectedVehicleId = 0
		// this means that the user selected the No vehicle option
		Vehicle assignedVehicle = null;
		if(selectedVehicleId != 0)
		{
			assignedVehicle = vehicleService.getVehicleById(selectedVehicleId);
		}
		if(portalDriver.getVehicle() == null)
		{
			if(assignedVehicle != null)
			{
				System.out.println("Vehicle was assigned during edit, update assigned vehicle for driver!");
				portalDriver.setVehicle(assignedVehicle);

				// we also must keep the vehicle records up-to-date, so the driver should also be set on the vehicle entity
				assignedVehicle.setDriver(portalDriver);
				vehicleService.updateVehicle(assignedVehicle);

				shouldUpdateDriver = true;

				System.out.println("Vehicle id: " + assignedVehicle.getId());
				System.out.println("Vehicle serial number: " + assignedVehicle.getDeviceSerialNumber());
				System.out.println("Vehicle vehicle number: " + assignedVehicle.getVehicleNumber());
				System.out.println("Vehicle vin: " + assignedVehicle.getVin());
				System.out.println("Vehicle vehicle model: " + assignedVehicle.getVehicleModel());
				System.out.println("Vehicle colour: " + assignedVehicle.getVehicleColour());
				System.out.println("Vehicle driver id: " + assignedVehicle.getDriver().getId());
			}
			else
			{
				System.out.println("No vehicle was assigned for driver!");
				// nothing to do here
			}
		}
		else
		{
			if(assignedVehicle == null)
			{
				System.out.println("The driver has no assigned vehicle anymore!");

				// we also must keep the vehicle records up-to-date, so before setting the driver's vehicle to null, 
				// previous vehicle's driver should also be set to null
				Vehicle previousVehicle = portalDriver.getVehicle();
				previousVehicle.setDriver(null);
				vehicleService.updateVehicle(previousVehicle);

				portalDriver.setVehicle(null);

				shouldUpdateDriver = true;
			}
			else
			{
				if(portalDriver.getVehicle().getId() == assignedVehicle.getId())
				{
					System.out.println("Assigned vehicle was not changed for driver!");
					// nothing to do here
				}
				else
				{
					System.out.println("Assigned vehicle changed for driver!!");

					// we also must keep the vehicle records up-to-date, so before setting the driver's vehicle to the new one received,
					// the previous vehicle's driver should be set to null
					Vehicle previousVehicle = portalDriver.getVehicle();
					previousVehicle.setDriver(null);
					vehicleService.updateVehicle(previousVehicle);

					// we also must keep the vehicle records up-to-date, so the driver should also be set on the current vehicle entity
					assignedVehicle.setDriver(portalDriver);
					vehicleService.updateVehicle(assignedVehicle);

					portalDriver.setVehicle(assignedVehicle);
					shouldUpdateDriver = true;

					System.out.println("Vehicle id: " + assignedVehicle.getId());
					System.out.println("Vehicle serial number: " + assignedVehicle.getDeviceSerialNumber());
					System.out.println("Vehicle vehicle number: " + assignedVehicle.getVehicleNumber());
					System.out.println("Vehicle vin: " + assignedVehicle.getVin());
					System.out.println("Vehicle vehicle model: " + assignedVehicle.getVehicleModel());
					System.out.println("Vehicle colour: " + assignedVehicle.getVehicleColour());
					System.out.println("Vehicle driver id: " + assignedVehicle.getDriver().getId());
				}
			}
		}

		if(shouldUpdateDriver)
		{
			System.out.println("Some driver characteristics were edited, update current driver record!");
			driverService.updateDriver(portalDriver);
		}

		sessionStatus.setComplete();

		return "redirect:/driver/" + id;
	}

	@RequestMapping("/driver/delete/{id}")
	public String deleteDriver(Model model, @PathVariable("id") int id) {
		System.out.println("/driver/delete/" + id + " GET request");

		// get driver from db
		Driver driver = driverService.getDriverById(id);
		System.out.println("Deleting driver with username: " + driver.getDriverUsername());

		// This method is called only after the user has been warned that he will loose all the driver information
		// if he proceeds (done inside manage_drivers.html) and has decided to proceed
		driverService.deleteDriver(id);

		return "redirect:/drivers";
	}

}
