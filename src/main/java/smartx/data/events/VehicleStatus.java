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
public class VehicleStatus {

	@Id
	@GeneratedValue
	@Column(unique = true)
	private int id;

	private long timestamp;
	private long odometer;
	private int speed;
	private long engineHours;

	@ManyToOne
	@JoinColumn
	private Vehicle vehicle;

	public VehicleStatus() {
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

	public long getOdometer() {
		return odometer;
	}
	public void setOdometer(long odometer) {
		this.odometer = odometer;
	}

	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public long getEngineHours() {
		return engineHours;
	}
	public void setEngineHours(long engineHours) {
		this.engineHours = engineHours;
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
		VehicleStatus vehicleStatus = (VehicleStatus) o;
		return (id == vehicleStatus.id &&
				Objects.equals(timestamp, vehicleStatus.timestamp) &&
				Objects.equals(odometer, vehicleStatus.odometer) &&
				Objects.equals(speed, vehicleStatus.speed) &&
				Objects.equals(engineHours, vehicleStatus.engineHours));
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, timestamp, odometer, speed, engineHours);
	}

}
