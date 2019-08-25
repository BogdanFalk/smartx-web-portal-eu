package smartx.ui.forms;

import java.time.LocalDateTime;

public class TableFormEngineStatus {

	private float convertedLatitude;
	private float convertedLongitude;
	private LocalDateTime convertedTimestamp;
	boolean value;
	
	public TableFormEngineStatus() {
		
	}

	public float getConvertedLatitude() {
		return convertedLatitude;
	}
	public void setConvertedLatitude(float convertedLatitude) {
		this.convertedLatitude = convertedLatitude;
	}

	public float getConvertedLongitude() {
		return convertedLongitude;
	}
	public void setConvertedLongitude(float convertedLongitude) {
		this.convertedLongitude = convertedLongitude;
	}

	public LocalDateTime getConvertedTimestamp() {
		return convertedTimestamp;
	}
	public void setConvertedTimestamp(LocalDateTime convertedTimestamp) {
		this.convertedTimestamp = convertedTimestamp;
	}

	public boolean isValue() {
		return value;
	}
	public void setValue(boolean value) {
		this.value = value;
	}
	
}
