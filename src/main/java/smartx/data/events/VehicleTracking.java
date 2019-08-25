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
public class VehicleTracking {

	@Id
	@GeneratedValue
	@Column(unique = true)
	private int id;
	
	private long timestamp;
	private int rawLatitude;
	private int rawLongitude;
	private int heading;

	@ManyToOne
	@JoinColumn
	private Vehicle vehicle;

	public VehicleTracking() {
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
	public void setRawLatitude(int latitude) {
		this.rawLatitude = latitude;
	}

	public int getRawLongitude() {
		return rawLongitude;
	}
	public void setRawLongitude(int longitude) {
		this.rawLongitude = longitude;
	}

	public int getHeading() {
		return heading;
	}
	public void setHeading(int heading) {
		this.heading = heading;
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
		VehicleTracking vehicleTracking = (VehicleTracking) o;
		return (id == vehicleTracking.id &&
				Objects.equals(timestamp, vehicleTracking.timestamp) &&
				Objects.equals(rawLatitude, vehicleTracking.rawLatitude) &&
				Objects.equals(rawLongitude, vehicleTracking.rawLongitude) &&
				Objects.equals(heading, vehicleTracking.heading));
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, timestamp, rawLatitude, rawLongitude, heading);
	}
	
}
