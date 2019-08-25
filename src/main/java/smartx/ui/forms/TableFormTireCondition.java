package smartx.ui.forms;

import java.time.LocalDateTime;

import smartx.data.events.TireCondition;

public class TableFormTireCondition {

	private float convertedLatitude;
	private float convertedLongitude;
	private LocalDateTime convertedTimestamp;
	private int pressureValue;
	private TireCondition.TirePressureType pressureType;
	private int tirePosition;
	private int temperatureValue;
	
	public TableFormTireCondition() {
		
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

	public int getPressureValue() {
		return pressureValue;
	}
	public void setPressureValue(int pressureValue) {
		this.pressureValue = pressureValue;
	}

	public TireCondition.TirePressureType getPressureType() {
		return pressureType;
	}
	public void setPressureType(TireCondition.TirePressureType pressureType) {
		this.pressureType = pressureType;
	}

	public int getTirePosition() {
		return tirePosition;
	}
	public void setTirePosition(int tirePosition) {
		this.tirePosition = tirePosition;
	}

	public int getTemperatureValue() {
		return temperatureValue;
	}
	public void setTemperatureValue(int temperatureValue) {
		this.temperatureValue = temperatureValue;
	}
	
}
