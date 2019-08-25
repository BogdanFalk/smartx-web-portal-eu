package smartx.ui.forms;

import java.time.LocalDateTime;

public class TableFormVehicleTracking {

	private float convertedLatitude;
	private float convertedLongitude;
	private LocalDateTime convertedTimestamp;
	private int heading;
	
	public TableFormVehicleTracking() {
		
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

	public int getHeading() {
		return heading;
	}
	public void setHeading(int heading) {
		this.heading = heading;
	}
	
}
