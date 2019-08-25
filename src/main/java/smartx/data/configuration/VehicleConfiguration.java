package smartx.data.configuration;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import smartx.data.vehicle.Vehicle;

@Entity
public class VehicleConfiguration {

	@Id
	@GeneratedValue
	@Column(unique = true)
	private int id;

	private long timestamp;
	private Boolean dtc;
	private Boolean fuelConsumption;
	private Boolean harshAcceleration;
	private Boolean harshBreak;
	private Boolean overRpm;
	private Boolean overSpeed;
	private Boolean tireCondition;
	private int onlineFrequencyInterval;
	private String description;

	@ManyToOne
	@JoinColumn
	private Vehicle vehicle;

	public VehicleConfiguration() {
		this.description = "";
		this.vehicle = new Vehicle();
		// set OFI by default to 5 so that this value is displayed when adding a new vehicle
		// if not, since th:field is used, we will initially display the default value for any int, which is 0
		this.onlineFrequencyInterval = 5;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public Boolean getDtc() {
		return dtc;
	}
	public void setDtc(Boolean dtc) {
		this.dtc = dtc;
	}

	public Boolean getFuelConsumption() {
		return fuelConsumption;
	}
	public void setFuelConsumption(Boolean fuelConsumption) {
		this.fuelConsumption = fuelConsumption;
	}

	public Boolean getHarshAcceleration() {
		return harshAcceleration;
	}
	public void setHarshAcceleration(Boolean harshAcceleration) {
		this.harshAcceleration = harshAcceleration;
	}

	public Boolean getHarshBreak() {
		return harshBreak;
	}
	public void setHarshBreak(Boolean harshBreak) {
		this.harshBreak = harshBreak;
	}

	public Boolean getOverRpm() {
		return overRpm;
	}
	public void setOverRpm(Boolean overRpm) {
		this.overRpm = overRpm;
	}

	public Boolean getOverSpeed() {
		return overSpeed;
	}
	public void setOverSpeed(Boolean overSpeed) {
		this.overSpeed = overSpeed;
	}

	public Boolean getTireCondition() {
		return tireCondition;
	}
	public void setTireCondition(Boolean tireCondition) {
		this.tireCondition = tireCondition;
	}

	public int getOnlineFrequencyInterval() {
		return onlineFrequencyInterval;
	}
	public void setOnlineFrequencyInterval(int onlineFrequencyInterval) {
		this.onlineFrequencyInterval = onlineFrequencyInterval;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}
	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VehicleConfiguration vehicleConfiguration = (VehicleConfiguration) o;
		return (id == vehicleConfiguration.id &&
				Objects.equals(dtc, vehicleConfiguration.dtc) &&
				Objects.equals(fuelConsumption, vehicleConfiguration.fuelConsumption) &&
				Objects.equals(harshAcceleration, vehicleConfiguration.harshAcceleration) &&
				Objects.equals(harshBreak, vehicleConfiguration.harshBreak) &&
				Objects.equals(overRpm, vehicleConfiguration.overRpm) &&
				Objects.equals(overSpeed, vehicleConfiguration.overSpeed) &&
				Objects.equals(tireCondition, vehicleConfiguration.tireCondition) &&
				Objects.equals(onlineFrequencyInterval, vehicleConfiguration.onlineFrequencyInterval));
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, timestamp, dtc, fuelConsumption, harshAcceleration, harshBreak, overRpm, overSpeed, tireCondition, onlineFrequencyInterval);
	}
	
	public boolean hasTheSameParameterConfiguration(VehicleConfiguration otherVehicleConfiguration)
	{
		return ((dtc == otherVehicleConfiguration.dtc) &&
				(fuelConsumption == otherVehicleConfiguration.fuelConsumption) &&
				(harshAcceleration == otherVehicleConfiguration.harshAcceleration) &&
				(harshBreak == otherVehicleConfiguration.harshBreak) &&
				(overRpm == otherVehicleConfiguration.overRpm) &&
				(overSpeed == otherVehicleConfiguration.overSpeed) &&
				(tireCondition == otherVehicleConfiguration.tireCondition) &&
				(onlineFrequencyInterval == otherVehicleConfiguration.onlineFrequencyInterval));
	}
	
	public boolean hasTheSameDescription(VehicleConfiguration otherVehicleConfiguration)
	{
		 return (description.equals(otherVehicleConfiguration.description));
	}
	
}
