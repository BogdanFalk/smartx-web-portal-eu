package smartx.ui.forms;

import smartx.data.driver.Driver;

public class TableFormDrivers {

	private String driverUsername;
	private String fullName;
	private String licenseNumber;
	private int driverId;
	private int vehicleId;
	private String vehicleDeviceSerialNumber;
	private String vehicleNumber;
	private String vehicleStatus;
	
	
	public TableFormDrivers() {
		this.driverUsername = "";
		this.fullName = "";
		this.licenseNumber = "";
		this.vehicleDeviceSerialNumber = "";
		this.vehicleNumber = "";
		this.vehicleStatus = "";
	}

	public String getDriverUsername() {
		return driverUsername;
	}
	public void setDriverUsername(String driverUsername) {
		this.driverUsername = driverUsername;
	}

	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}
	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public int getDriverId() {
		return driverId;
	}
	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}

	public int getVehicleId() {
		return vehicleId;
	}
	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getVehicleDeviceSerialNumber() {
		return vehicleDeviceSerialNumber;
	}
	public void setVehicleDeviceSerialNumber(String vehicleDeviceSerialNumber) {
		this.vehicleDeviceSerialNumber = vehicleDeviceSerialNumber;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}
	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getVehicleStatus() {
		return vehicleStatus;
	}
	public void setVehicleStatus(String vehicleStatus) {
		this.vehicleStatus = vehicleStatus;
	}
	
	public void populateTableFormDrivers(Driver displayedDriver) {
		if(displayedDriver != null)
		{
			this.driverUsername = displayedDriver.getDriverUsername();
			this.fullName = displayedDriver.getFullName();
			this.licenseNumber = displayedDriver.getLicenseNumber();
			this.driverId = displayedDriver.getId();
		}
		if(displayedDriver.getVehicle() != null)
		{
			this.vehicleId = displayedDriver.getVehicle().getId();
			this.vehicleDeviceSerialNumber = displayedDriver.getVehicle().getDeviceSerialNumber();
			this.vehicleNumber = displayedDriver.getVehicle().getVehicleNumber();
			this.vehicleStatus = displayedDriver.getVehicle().getStatus();
		}

	}
	
}
