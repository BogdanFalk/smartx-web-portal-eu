package smartx.ui.forms;

import smartx.data.configuration.DeviceConfiguration;
import smartx.data.vehicle.Vehicle;

public class TableFormVehicles {

	private String deviceSerialNumber;
	private String vin;
	private String status;
	private String vehicleNumber;
	private String simiccid;
	private int vehicleId;
	private int driverId;
	private String driverUsername;

	public TableFormVehicles() {
		this.deviceSerialNumber = "";
		this.vin = "";
		this.status = "";
		this.vehicleNumber = "";
		this.simiccid = "";
		this.driverUsername = "";
	}

	public String getDeviceSerialNumber() {
		return deviceSerialNumber;
	}
	public void setDeviceSerialNumber(String deviceSerialNumber) {
		this.deviceSerialNumber = deviceSerialNumber;
	}

	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}
	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getSimiccid() {
		return simiccid;
	}
	public void setSimiccid(String simiccid) {
		this.simiccid = simiccid;
	}

	public int getVehicleId() {
		return vehicleId;
	}
	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}

	public int getDriverId() {
		return driverId;
	}
	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}

	public String getDriverUsername() {
		return driverUsername;
	}
	public void setDriverUsername(String driverUsername) {
		this.driverUsername = driverUsername;
	}

	public void populateTableFormVehicles(Vehicle displayedVehicle, DeviceConfiguration latestDeviceConfiguration) {
		if(displayedVehicle != null)
		{
			this.deviceSerialNumber = displayedVehicle.getDeviceSerialNumber();
			this.vin = displayedVehicle.getVin();
			this.vehicleNumber = displayedVehicle.getVehicleNumber();
			this.status = displayedVehicle.getStatus();
			this.vehicleId = displayedVehicle.getId();
		}
		if(latestDeviceConfiguration != null)
		{
			this.simiccid = latestDeviceConfiguration.getSimiccid();
		}
		if(displayedVehicle.getDriver() != null)
		{
			this.driverId = displayedVehicle.getDriver().getId();
			this.driverUsername = displayedVehicle.getDriver().getDriverUsername();
		}

	}
}
