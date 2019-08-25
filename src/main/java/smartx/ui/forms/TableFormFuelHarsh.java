package smartx.ui.forms;

import java.time.LocalDateTime;

public class TableFormFuelHarsh {

	private float convertedLatitude;
	private float convertedLongitude;
	private LocalDateTime convertedTimestamp;
	long value;
	
	public TableFormFuelHarsh() {
		
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

	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}
	
}
