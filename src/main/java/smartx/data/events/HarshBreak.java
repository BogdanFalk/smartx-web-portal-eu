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
public class HarshBreak {

	@Id
	@GeneratedValue
	@Column(unique = true)
	private int id;

	private long timestamp;
	private int rawLatitude;
	private int rawLongitude;
	private int value;

	@ManyToOne
	@JoinColumn
	private Vehicle vehicle;

	public HarshBreak() {
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
		HarshBreak harshBreak = (HarshBreak) o;
		return (id == harshBreak.id &&
				Objects.equals(timestamp, harshBreak.timestamp) &&
				Objects.equals(rawLatitude, harshBreak.rawLatitude) &&
				Objects.equals(rawLongitude, harshBreak.rawLongitude) &&
				Objects.equals(value, harshBreak.value));
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, timestamp, rawLatitude, rawLongitude, value);
	}
	
}
