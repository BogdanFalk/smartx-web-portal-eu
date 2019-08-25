package smartx.data.events;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import smartx.data.vehicle.Vehicle;

@Entity
public class TireCondition {

	public enum TirePressureType {
		eTirePressureType_LowAirPressure,
		eTirePressureType_HighAirPressure,
		eTirePressureType_Normal,
		eTirePressureType_UpperBoundary
	}

	@Id
	@GeneratedValue
	@Column(unique = true)
	private int id;

	private long timestamp;
	private int rawLatitude;
	private int rawLongitude;
	private int pressureValue;
	private TirePressureType pressureType;
	private int tirePosition;
	private int temperatureValue;
	
	@ManyToOne
	@JoinColumn
	private Vehicle vehicle;

	public TireCondition() {
		this.vehicle = new Vehicle();
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public int getRawLatitude() {
		return rawLatitude;
	}
	public void setRawLatitude(int rawLatitude) {
		this.rawLatitude = rawLatitude;
	}

	public int getRawLongitude() {
		return rawLongitude;
	}
	public void setRawLongitude(int rawLongitude) {
		this.rawLongitude = rawLongitude;
	}

	public int getPressureValue() {
		return pressureValue;
	}
	public void setPressureValue(int pressureValue) {
		this.pressureValue = pressureValue;
	}

	public TirePressureType getPressureType() {
		return pressureType;
	}
	public void setPressureType(TirePressureType pressureType) {
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

	public Vehicle getVehicle() {
		return vehicle;
	}
	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TireCondition tireCondition = (TireCondition) o;
		return (id == tireCondition.id &&
				Objects.equals(timestamp, tireCondition.timestamp) &&
				Objects.equals(rawLatitude, tireCondition.rawLatitude) &&
				Objects.equals(rawLongitude, tireCondition.rawLongitude) &&
				Objects.equals(pressureValue, tireCondition.pressureValue) &&
				Objects.equals(pressureType, tireCondition.pressureType) &&
				Objects.equals(tirePosition, tireCondition.tirePosition) &&
				Objects.equals(temperatureValue, tireCondition.temperatureValue));
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, timestamp, rawLatitude, rawLongitude, pressureValue, pressureType, tirePosition, temperatureValue);
	}
	
}
