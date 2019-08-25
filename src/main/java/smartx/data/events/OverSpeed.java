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
public class OverSpeed {

	@Id
	@GeneratedValue
	@Column(unique = true)
	private int id;

	private long timestamp;
	private int rawLatitude;
	private int rawLongitude;
	private int duration;
	private int value;
	
	@ManyToOne
	@JoinColumn
	private Vehicle vehicle;

	public OverSpeed() {
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
		OverSpeed overSpeed = (OverSpeed) o;
		return (id == overSpeed.id &&
				Objects.equals(timestamp, overSpeed.timestamp) &&
				Objects.equals(rawLatitude, overSpeed.rawLatitude) &&
				Objects.equals(rawLongitude, overSpeed.rawLongitude) &&
				Objects.equals(duration, overSpeed.duration) &&
				Objects.equals(value, overSpeed.value));
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, timestamp, rawLatitude, rawLongitude, duration, value);
	}
	
}
