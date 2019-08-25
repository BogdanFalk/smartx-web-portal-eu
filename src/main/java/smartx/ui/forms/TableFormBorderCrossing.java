package smartx.ui.forms;

import java.time.LocalDateTime;

public class TableFormBorderCrossing {

	private float convertedLatitude;
	private float convertedLongitude;
	private LocalDateTime convertedTimestamp;
	private String oldCountry;
	private String newCountry;
	
	public TableFormBorderCrossing() {
		
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

	public String getOldCountry() {
		return oldCountry;
	}
	public void setOldCountry(String oldCountry) {
		this.oldCountry = oldCountry;
	}

	public String getNewCountry() {
		return newCountry;
	}
	public void setNewCountry(String newCountry) {
		this.newCountry = newCountry;
	}
	
}
