package smartx.businesslogic.portalmessageobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import smartx.businesslogic.MessageType;

public class PortalExtractLogsObject extends PortalMessageObject {

	private String serialNumber;
	private String url;
	private long startTimestamp;
	private long endTimestamp;
	boolean asw;
	boolean bsw;
	boolean framework;
	boolean database;
	boolean configurationFiles;
	
	public PortalExtractLogsObject() {
		this.serialNumber = "";
		this.url = "";
	}

	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public long getStartTimestamp() {
		return startTimestamp;
	}
	public void setStartTimestamp(long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public long getEndTimestamp() {
		return endTimestamp;
	}
	public void setEndTimestamp(long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public boolean isAsw() {
		return asw;
	}
	public void setAsw(boolean asw) {
		this.asw = asw;
	}

	public boolean isBsw() {
		return bsw;
	}
	public void setBsw(boolean bsw) {
		this.bsw = bsw;
	}

	public boolean isFramework() {
		return framework;
	}
	public void setFramework(boolean framework) {
		this.framework = framework;
	}

	public boolean isDatabase() {
		return database;
	}
	public void setDatabase(boolean database) {
		this.database = database;
	}

	public boolean isConfigurationFiles() {
		return configurationFiles;
	}
	public void setConfigurationFiles(boolean configurationFiles) {
		this.configurationFiles = configurationFiles;
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
