package smartx.ui.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import smartx.businesslogic.vehicletracking.GMapsCoordinates;
import smartx.data.driver.DriverService;
import smartx.data.events.VehicleTracking;
import smartx.data.events.VehicleTrackingService;
import smartx.data.user.User;
import smartx.data.user.UserService;
import smartx.data.vehicle.Vehicle;
import smartx.data.vehicle.VehicleService;
import smartx.ui.forms.TableFormMostRecentEvents;
import smartx.utils.PassEncoding;
import smartx.utils.Roles;
import smartx.utils.Status;

@Controller
@ComponentScan
@SessionAttributes({"editedAccount"}) // DO NOT DELETE - Used for editing account
public class HomeController {

	@Autowired
	GlobalController globalController;
	@Autowired
	VehicleService vehicleService;
	@Autowired
	UserService userService;
	@Autowired
	DriverService driverService;
	@Autowired
	VehicleTrackingService vehicleTrackingService;

	// Since this model attribute is declared as a session attribute, when the session expires (if being left inactive 
	// for a certain amount of time), the object also expires, so it needs to be reinitialized
	@ModelAttribute("editedAccount")
	public User populateEditedAccount() {
		System.out.println("Session expired, reinitializing session attribute editedAccount!");   
		return new User(); // populates editedAccount for the first time if its null
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

	@RequestMapping("/register")
	public String handleRegistrationPage(Model model) {
		System.out.println("/register GET request");
		if(globalController.getLoginUser() != null)
		{
			return handleHomePage(model);
		}
		else
		{
			model.addAttribute("reqUser", new User());
			System.out.println("driver NOT logged in => register");
			return "register";
		}
	}

	@RequestMapping(value = {"/user/register"}, method = RequestMethod.POST)
	public String registerUser(@ModelAttribute("reqUser") User reqUser,
			final RedirectAttributes redirectAttributes) {

		System.out.println("/user/register POST request");
		User user = userService.getUserByUsername(reqUser.getUsername());
		if (user != null) {
			redirectAttributes.addFlashAttribute("saveUser", "exist-name");
			return "redirect:/register";
		}

		reqUser.setPassword(PassEncoding.getInstance().passwordEncoder.encode(reqUser.getPassword()));
		reqUser.setRole(Roles.ROLE_USER.getValue());

		if (userService.addUser(reqUser) != null) {
			redirectAttributes.addFlashAttribute("saveUser", "success");
			return "redirect:/login";
		} 
		else 
		{
			redirectAttributes.addFlashAttribute("saveUser", "fail");
			return "redirect:/register";
		}
	}

	@RequestMapping("/login")
	public String handleLoginPage(Model model) {
		System.out.println("/login GET request");
		if(globalController.getLoginUser() != null)
		{
			return handleHomePage(model);
		}
		else
		{
			System.out.println("driver NOT logged in => login");
			return "login";
		}

	}

	@RequestMapping("/")
	public String handleRoot(Model model) {
		System.out.println("/ GET request");
		return handleHomePage(model);
	}

	@RequestMapping("/home")
	public String handleHomePage(Model model) {
		System.out.println("/home GET request");
		// The getLoginUser method should only be used for retrieving the id of the logged in user, not the user entity, because the User record used inside
		// GlobalController is not always up-to-date
		User sessionUser = globalController.getLoginUser();
		if(sessionUser != null)
		{
			int currentUserId = sessionUser.getId();
			User loggedInUser = userService.getUserById(currentUserId);

			System.out.println("driver logged in => home");

			// Get the list of vehicles with status AVAILABLE.
			List<Vehicle> userAvailableAndUnavailableVehicles = vehicleService.getVehiclesByUserIdAndStatus(loggedInUser.getId(),Status.AVAILABLE.getValue());
			// Append the vehicles with status UNAVAILABLE.
			userAvailableAndUnavailableVehicles.addAll(vehicleService.getVehiclesByUserIdAndStatus(loggedInUser.getId(),Status.UNAVAILABLE.getValue()));

			List<VehicleTracking> lastVehicleTrackings = new ArrayList<>();

			int vehicleId = -1;
			VehicleTracking lastVehicleTracking = null;

			for (Vehicle vehicle : userAvailableAndUnavailableVehicles)
			{
				System.out.println("Process vehicle tracking for vehicle with id: " + vehicle.getId() + " and status: " + vehicle.getStatus());

				vehicleId = vehicle.getId();
				lastVehicleTracking = vehicleTrackingService.getMostRecentVehicleTrackingByVehicleId(vehicleId);

				if ( lastVehicleTracking != null )
				{
					lastVehicleTrackings.add(lastVehicleTracking);
					System.out.println("Add vehicle tracking for vehicle with id: " + vehicleId + ", status: " + vehicle.getStatus() + " and coords: " 
							+ lastVehicleTracking.getRawLatitude() + ", " + lastVehicleTracking.getRawLongitude());
				}
			}

			final List<GMapsCoordinates> lastGoogleMapsVehicleTracking = vehicleTrackingService.getGoogleMapsCoordinatesFroVehicleTrackingEvents(lastVehicleTrackings);
			System.out.println("Number of Google Maps coordinates got from vehicle trackings: " + lastGoogleMapsVehicleTracking.size());
			model.addAttribute("gmapsVehicleTrackings", lastGoogleMapsVehicleTracking);

			// Get the list of vehicles with status AVAILABLE.
			List<Vehicle> availableVehiclesList = vehicleService.getVehiclesByUserIdAndStatus(loggedInUser.getId(),Status.AVAILABLE.getValue());

			model.addAttribute("availableVehiclesList", availableVehiclesList);

			// Get the list of all the user's vehicles
			List<Vehicle> allUserVehicles = vehicleService.getVehiclesByUserId(loggedInUser.getId());
			System.out.println("Number of vehicles: " + allUserVehicles.size());

			// Initialize the events list
			List<TableFormMostRecentEvents> allVehiclesRecentEventsList = new ArrayList<TableFormMostRecentEvents>();

			// for each of the user's vehicles, get the most recent event of each type, construct TableFormMostRecentEvents from it and 
			// append it to the list
			for (Vehicle vehicle : allUserVehicles)
			{
				allVehiclesRecentEventsList.addAll(vehicleService.getMostRecentsEventDetailsTablesForVehicle(vehicle.getId(), vehicle.getVehicleNumber()));
			}

			// sort the list
			Collections.sort(allVehiclesRecentEventsList, Collections.reverseOrder(Comparator.comparingLong(TableFormMostRecentEvents::getTimestamp)));

			System.out.println("Total number of events from all the user's vehicles: " + allVehiclesRecentEventsList.size());
			model.addAttribute("allVehiclesRecentEventsList", allVehiclesRecentEventsList);

			model.addAttribute("numberOfAvailableVehicles", vehicleService.getVehiclesByUserIdAndStatus(loggedInUser.getId(),Status.AVAILABLE.getValue()).size());
			model.addAttribute("numberOfUnavailableVehicles", vehicleService.getVehiclesByUserIdAndStatus(loggedInUser.getId(),Status.UNAVAILABLE.getValue()).size());
			model.addAttribute("numberOfNotRegisteredVehicles", vehicleService.getVehiclesByUserIdAndStatus(loggedInUser.getId(),Status.NOT_REGISTERED.getValue()).size());

			return "home";
		}
		else
		{
			System.out.println("driver NOT logged in => login");
			return "login";
		}
	}

	@RequestMapping("/account")
	public String handleAccountPage(Model model) {
		System.out.println("/account GET request");

		// The getLoginUser method should only be used for retrieving the id of the logged in user, not the user entity, because the User record used inside
		// GlobalController is not always up-to-date
		int currentUserId = globalController.getLoginUser().getId();
		User currentUser = userService.getUserById(currentUserId);

		// get the number of vehicles and drivers belonging to the logged in driver
		int numberOfVehicles = vehicleService.getVehiclesByUserId(currentUserId).size();
		int numberOfDrivers = driverService.getDriversByUserId(currentUserId).size();

		// add the user image / null if the user did not upload one
		if (currentUser.getPicture() != null) 
		{
			String zUserImage = Base64.getEncoder().encodeToString(currentUser.getPicture());

			model.addAttribute("userImage", zUserImage);
		} 
		else 
		{
			model.addAttribute("userImage", null);
		}

		model.addAttribute("currentUser", currentUser);
		model.addAttribute("numberOfVehicles", numberOfVehicles);
		model.addAttribute("numberOfDrivers", numberOfDrivers);

		return "account";
	}

	@RequestMapping("/account/edit")
	public String handleEditAccount(Model model) {
		System.out.println("/account/edit/" + " GET request");

		// get user from db
		// The getLoginUser method should only be used for retrieving the id of the logged in user, not the user entity, because the User record used inside
		// GlobalController is not always up-to-date
		int currentUserId = globalController.getLoginUser().getId();
		User currentUser = userService.getUserById(currentUserId);

		// get the number of vehicles and drivers belonging to the logged in driver
		int numberOfVehicles = vehicleService.getVehiclesByUserId(currentUserId).size();
		int numberOfDrivers = driverService.getDriversByUserId(currentUserId).size();

		// add the user image / null if the user did not upload one
		if (currentUser.getPicture() != null) 
		{
			String zUserImage = Base64.getEncoder().encodeToString(currentUser.getPicture());

			model.addAttribute("userImage", zUserImage);
		} 
		else 
		{
			model.addAttribute("userImage", null);
		}

		model.addAttribute("editedAccount", currentUser);
		model.addAttribute("numberOfVehicles", numberOfVehicles);
		model.addAttribute("numberOfDrivers", numberOfDrivers);

		return "edit_account";
	}

	@RequestMapping(value = {"/account/edit"}, method = RequestMethod.POST)
	public String editAccount(@ModelAttribute("editedAccount") User editedAccount, 
			@RequestParam("userImage") MultipartFile file,
			SessionStatus sessionStatus,
			final RedirectAttributes redirectAttributes) {
		System.out.println("/account/edit/" + " POST request");

		// get initial user account from db
		// The getLoginUser method should only be used for retrieving the id of the logged in user, not the user entity, because the User record used inside
		// GlobalController is not always up-to-date
		User portalUser = userService.getUserById(globalController.getLoginUser().getId());

		// 1) CHECK USER DETAILS

		// if the user's company name, DOT number, address or e-mail was changed, the current user from db should be updated
		boolean shouldUpdateUser = false;

		if(!editedAccount.getCompanyDotNumber().equals(portalUser.getCompanyDotNumber()))
		{
			System.out.println("Some user characteristics were edited, update current user record!2");
			portalUser.setCompanyDotNumber(editedAccount.getCompanyDotNumber());
			shouldUpdateUser = true;
		}
		if(!editedAccount.getCompanyName().equals(portalUser.getCompanyName()))
		{
			System.out.println("Some user characteristics were edited, update current user record!1");
			portalUser.setCompanyName(editedAccount.getCompanyName());
			shouldUpdateUser = true;
		}
		if(!editedAccount.getCompanyAddress().equals(portalUser.getCompanyAddress()))
		{
			System.out.println("Some user characteristics were edited, update current user record!3");
			portalUser.setCompanyAddress(editedAccount.getCompanyAddress());
			shouldUpdateUser = true;
		}

		// check if a different picture was uploaded
		if ((file.getOriginalFilename() != null) && (!file.getOriginalFilename().isEmpty()) && (!file.isEmpty())) 
		{
			System.out.println("Uploaded file name: " + file.getOriginalFilename());
			try 
			{
				byte[] fileContents = file.getBytes();

				portalUser.setPicture(fileContents);

				shouldUpdateUser = true;

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

		if(shouldUpdateUser)
		{
			System.out.println("Some user characteristics were edited, update current user record!");
			userService.updateUser(portalUser);
		}

		return "redirect:/account";
	}

}
