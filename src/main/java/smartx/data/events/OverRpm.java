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
public class OverRpm {

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

	public OverRpm() {
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
		OverRpm overRpm = (OverRpm) o;
		return (id == overRpm.id &&
				Objects.equals(timestamp, overRpm.timestamp) &&
				Objects.equals(rawLatitude, overRpm.rawLatitude) &&
				Objects.equals(rawLongitude, overRpm.rawLongitude) &&
				Objects.equals(duration, overRpm.duration) &&
				Objects.equals(value, overRpm.value));
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, timestamp, rawLatitude, rawLongitude, duration, value);
	}
	
}
