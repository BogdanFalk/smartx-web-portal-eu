package smartx.ui.forms;

import java.time.LocalDateTime;

public class TableFormOver {

	private float convertedLatitude;
	private float convertedLongitude;
	private LocalDateTime convertedTimestamp;
	private int duration;
	private int value;
	
	public TableFormOver() {
		
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

	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
}
