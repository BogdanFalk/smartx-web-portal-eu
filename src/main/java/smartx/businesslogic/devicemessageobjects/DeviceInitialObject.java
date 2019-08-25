package smartx.businesslogic.devicemessageobjects;

public class DeviceInitialObject extends DeviceMessageObject {

	private String vin;
	private String url;
	private String aswVersion;
	private String frameworkVersion;
	private String bswVersion;
	private String simiccid;
	private String serialNumber;
	
	public DeviceInitialObject() {
		this.vin = "";
		this.url = "";
		this.aswVersion = "";
		this.frameworkVersion = "";
		this.bswVersion = "";
		this.simiccid = "";
		this.serialNumber = "";
	}
	
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getAswVersion() {
		return aswVersion;
	}
	public void setAswVersion(String aswVersion) {
		this.aswVersion = aswVersion;
	}
	
	public String getFrameworkVersion() {
		return frameworkVersion;
	}
	public void setFrameworkVersion(String frameworkVersion) {
		this.frameworkVersion = frameworkVersion;
	}
	
	public String getBswVersion() {
		return bswVersion;
	}
	public void setBswVersion(String bswVersion) {
		this.bswVersion = bswVersion;
	}
	
	public String getSimiccid() {
		return simiccid;
	}
	public void setSimiccid(String simiccid) {
		this.simiccid = simiccid;
	}
	
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
}
