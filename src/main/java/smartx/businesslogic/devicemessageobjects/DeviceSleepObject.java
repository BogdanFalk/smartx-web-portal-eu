package smartx.businesslogic.devicemessageobjects;

public class DeviceSleepObject extends DeviceMessageObject {

	private String serialNumber;
	private long timestamp;
	
	public DeviceSleepObject() {
		this.serialNumber = "";
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
}
