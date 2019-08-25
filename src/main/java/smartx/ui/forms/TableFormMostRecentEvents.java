package smartx.ui.forms;

import java.time.LocalDateTime;

public class TableFormMostRecentEvents {

	private String vehicleNumber;
	private long timestamp;
	private LocalDateTime localDateTime;
	private String typeName;
	private int typeValue;
	private float latitude;
	private float longitude;
	
	public TableFormMostRecentEvents() {
		this.vehicleNumber = "";
		this.typeName = "";
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}
	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public LocalDateTime getLocalDateTime() {
		return localDateTime;
	}
	public void setLocalDateTime(LocalDateTime localDateTime) {
		this.localDateTime = localDateTime;
	}

	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String type) {
		this.typeName = type;
	}

	public int getTypeValue() {
		return typeValue;
	}
	public void setTypeValue(int typeValue) {
		this.typeValue = typeValue;
	}

	public float getLatitude() {
		return latitude;
	}
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	
}
