package smartx.businesslogic.portalmessageobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import smartx.businesslogic.MessageType;

public class PortalConfigurationObject extends PortalMessageObject {

	private String serialNumber;
	private String vin;
	private String vehicleNumber;
	private CalibrationMessage calibrationMessage;
	private int onlineFrequencyInterval;

	public PortalConfigurationObject() {
		this.serialNumber = "";
		this.vin = "";
		this.vehicleNumber = "";
	}

	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}
	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public CalibrationMessage getCalibrationMessage() {
		return calibrationMessage;
	}
	public void setCalibrationMessage(CalibrationMessage calibrationMessage) {
		this.calibrationMessage = calibrationMessage;
	}

	public int getOnlineFrequencyInterval() {
		return onlineFrequencyInterval;
	}
	public void setOnlineFrequencyInterval(int onlineFrequencyInterval) {
		this.onlineFrequencyInterval = onlineFrequencyInterval;
	}

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
