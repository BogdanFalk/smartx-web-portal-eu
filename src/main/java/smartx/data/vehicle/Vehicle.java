package smartx.data.vehicle;

import smartx.data.driver.Driver;
import smartx.data.user.User;
import smartx.utils.Status;

import javax.persistence.*;

import java.util.Objects;


@Entity()
public class Vehicle {

	@Id
	@GeneratedValue
	@Column(unique = true)
	private int id;

	@Column(unique = true)
	private String deviceSerialNumber;
	private String vin;
	private String status;
	private String vehicleNumber;
	private String vehicleModel;
	private String vehicleColour;
	private long creationTimestamp;
	private long lastReportedTimestamp;
	private boolean exportLogsInProgress;
	@Lob
    private byte[] picture;

	@ManyToOne
	@JoinColumn
	private User user;
	
	@OneToOne()
    @JoinColumn(name = "driver_id", referencedColumnName = "id")
    private Driver driver;
	

	public Vehicle() {
		this.deviceSerialNumber = "";
		this.vin = "";
		this.vehicleNumber = "";
		this.vehicleModel = "";
		this.vehicleColour = "";
		this.status = Status.NOT_REGISTERED.getValue();
		this.user = new User();
		this.driver = null;
		this.lastReportedTimestamp = 0;
		this.exportLogsInProgress = false;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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

	public String getVehicleModel() {
		return vehicleModel;
	}
	public void setVehicleModel(String vehicleModel) {
		this.vehicleModel = vehicleModel;
	}

	public String getVehicleColour() {
		return vehicleColour;
	}
	public void setVehicleColour(String vehicleColour) {
		this.vehicleColour = vehicleColour;
	}

	public long getCreationTimestamp() {
		return creationTimestamp;
	}
	public void setCreationTimestamp(long creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}
	
	public long getLastReportedTimestamp() {
		return lastReportedTimestamp;
	}

	public void setLastReportedTimestamp(long lastReportedTimestamp) {
		this.lastReportedTimestamp = lastReportedTimestamp;
	}

	public boolean isExportLogsInProgress() {
		return exportLogsInProgress;
	}
	public void setExportLogsInProgress(boolean exportLogsInProgress) {
		this.exportLogsInProgress = exportLogsInProgress;
	}
	
	public byte[] getPicture() {
 		return picture;
 	}
  	public void setPicture(byte[] picture) {
 		this.picture = picture;
 	}

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public Driver getDriver() {
		return driver;
	}
	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Vehicle vehicle = (Vehicle) o;
		return (id == vehicle.id &&
				Objects.equals(deviceSerialNumber, vehicle.deviceSerialNumber) //&&
//				Objects.equals(vin, vehicle.vin) &&
//				Objects.equals(status, vehicle.status) &&
//				Objects.equals(vehicleNumber, vehicle.vehicleNumber) &&
//				Objects.equals(vehicleModel, vehicle.vehicleModel) &&
//				Objects.equals(vehicleColour, vehicle.vehicleColour) &&
//				Objects.equals(creationTimestamp, vehicle.creationTimestamp)
				);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, deviceSerialNumber, vin, status, vehicleNumber, vehicleModel, 
				vehicleColour, creationTimestamp, lastReportedTimestamp, exportLogsInProgress);
	}
}
