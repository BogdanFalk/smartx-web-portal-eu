package smartx.businesslogic.devicemessageobjects;


public class DeviceConfigurationFinishedObject extends DeviceMessageObject {

	private String serialNumber;
	private String vin;
	private boolean vinStatus;
	
	public DeviceConfigurationFinishedObject() {
		this.serialNumber = "";
		this.vin = "";
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
	
	public boolean isVinStatus() {
		return vinStatus;
	}
	public void setVinStatus(boolean vinStatus) {
		this.vinStatus = vinStatus;
	}
	
}
