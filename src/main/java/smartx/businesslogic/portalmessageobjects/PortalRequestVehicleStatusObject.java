package smartx.businesslogic.portalmessageobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import smartx.businesslogic.MessageType;

public class PortalRequestVehicleStatusObject extends PortalMessageObject {

	private String serialNumber;
	private String vehicleNumber;
	private boolean vehicleTracking;
	private boolean engineStatus;
	private boolean tireCondition;
	private boolean fuelConsumption;
	private boolean dtc;
	private boolean overSpeed;
	private boolean overRpm;
	private boolean harshAcceleration;
	private boolean harshBreak;
	private boolean borderCrossing;
	private boolean odometer;
	private boolean speed;
	private boolean engineHours;


	public PortalRequestVehicleStatusObject() {
		this.serialNumber = "";
		this.vehicleNumber = "";
	}

	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}
	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public boolean isVehicleTracking() {
		return vehicleTracking;
	}
	public void setVehicleTracking(boolean vehicleTracking) {
		this.vehicleTracking = vehicleTracking;
	}

	public boolean isEngineStatus() {
		return engineStatus;
	}
	public void setEngineStatus(boolean engineStatus) {
		this.engineStatus = engineStatus;
	}

	public boolean isTireCondition() {
		return tireCondition;
	}
	public void setTireCondition(boolean tireCondition) {
		this.tireCondition = tireCondition;
	}

	public boolean isFuelConsumption() {
		return fuelConsumption;
	}
	public void setFuelConsumption(boolean fuelConsumption) {
		this.fuelConsumption = fuelConsumption;
	}

	public boolean isDtc() {
		return dtc;
	}
	public void setDtc(boolean dtc) {
		this.dtc = dtc;
	}

	public boolean isOverSpeed() {
		return overSpeed;
	}
	public void setOverSpeed(boolean overSpeed) {
		this.overSpeed = overSpeed;
	}

	public boolean isOverRpm() {
		return overRpm;
	}
	public void setOverRpm(boolean overRpm) {
		this.overRpm = overRpm;
	}

	public boolean isHarshAcceleration() {
		return harshAcceleration;
	}
	public void setHarshAcceleration(boolean harshAcceleration) {
		this.harshAcceleration = harshAcceleration;
	}

	public boolean isHarshBreak() {
		return harshBreak;
	}
	public void setHarshBreak(boolean harshBreak) {
		this.harshBreak = harshBreak;
	}

	public boolean isBorderCrossing() {
		return borderCrossing;
	}
	public void setBorderCrossing(boolean borderCrossing) {
		this.borderCrossing = borderCrossing;
	}

	public boolean isOdometer() {
		return odometer;
	}
	public void setOdometer(boolean odometer) {
		this.odometer = odometer;
	}

	public boolean isSpeed() {
		return speed;
	}
	public void setSpeed(boolean speed) {
		this.speed = speed;
	}

	public boolean isEngineHours() {
		return engineHours;
	}
	public void setEngineHours(boolean engineHours) {
		this.engineHours = engineHours;
	}

	public void setAll(boolean setToTrue)
	{
		if(setToTrue)
		{
			this.vehicleTracking = true;
			this.engineStatus = true;
			this.tireCondition = true;
			this.fuelConsumption = true;
			this.dtc = true;
			this.overSpeed = true;
			this.overRpm = true;
			this.harshAcceleration = true;
			this.harshBreak = true;
			this.borderCrossing = true;
			this.odometer = true;
			this.speed = true;
			this.engineHours = true;
		}
		else
		{
			this.vehicleTracking = false;
			this.engineStatus = false;
			this.tireCondition = false;
			this.fuelConsumption = false;
			this.dtc = false;
			this.overSpeed = false;
			this.overRpm = false;
			this.harshAcceleration = false;
			this.harshBreak = false;
			this.borderCrossing = false;
			this.odometer = false;
			this.speed = false;
			this.engineHours = false;
		}
	}

	// the methods from the superclass have to be ignored when serializing the json
	@Override
	@JsonIgnore
	public int getSequenceNumber() {
		return super.getSequenceNumber();
	};

	@Override
	@JsonIgnore
	public String getDeviceSerialNumber() {
		return super.getDeviceSerialNumber();
	};

	@Override
	@JsonIgnore
	public MessageType getMessageType() {
		return super.getMessageType();
	};

	@Override
	@JsonIgnore
	public String getJsonMessage() {
		return super.getJsonMessage();
	};

}
