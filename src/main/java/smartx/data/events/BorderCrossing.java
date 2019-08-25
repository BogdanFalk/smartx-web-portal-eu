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
public class BorderCrossing {

	@Id
	@GeneratedValue
	@Column(unique = true)
	private int id;

	private long timestamp;
	private int rawLatitude;
	private int rawLongitude;
	private String oldCountry;
	private String newCountry;

	@ManyToOne
	@JoinColumn
	private Vehicle vehicle;

	public BorderCrossing() {
		this.oldCountry = "";
		this.newCountry = "";
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
		BorderCrossing borderCrossing = (BorderCrossing) o;
		return (id == borderCrossing.id &&
				Objects.equals(timestamp, borderCrossing.timestamp) &&
				Objects.equals(rawLatitude, borderCrossing.rawLatitude) &&
				Objects.equals(rawLongitude, borderCrossing.rawLongitude) &&
				Objects.equals(oldCountry, borderCrossing.oldCountry) &&
				Objects.equals(newCountry, borderCrossing.newCountry));
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, timestamp, rawLatitude, rawLongitude, oldCountry, newCountry);
	}

}
