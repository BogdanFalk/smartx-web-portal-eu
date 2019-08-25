package smartx.businesslogic.devicemessageobjects;

public class DeviceExtractLogsResponseObject extends DeviceMessageObject {

	private String serialNumber;
	private boolean uploadSuccessful;
	
	public DeviceExtractLogsResponseObject() {
		this.serialNumber = "";
	}

	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public boolean isUploadSuccessful() {
		return uploadSuccessful;
	}
	public void setUploadSuccessful(boolean uploadSuccessful) {
		this.uploadSuccessful = uploadSuccessful;
	}

}
