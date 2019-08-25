package smartx.data.configuration;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import smartx.data.vehicle.Vehicle;

@Entity
public class DeviceConfiguration {

	@Id
	@GeneratedValue
	@Column(unique = true)
	private int id;

	private long timestamp;
	private String url;
	private String aswVersion;
	private String frameworkVersion;
	private String bswVersion;
	private String simiccid;

	@ManyToOne
	@JoinColumn
	private Vehicle vehicle;

	public DeviceConfiguration() {
		this.url = "";
		this.aswVersion = "";
		this.frameworkVersion = "";
		this.bswVersion = "";
		this.simiccid = "";
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

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public String getAswVersion() {
		return aswVersion;
	}
	public void setAswVersion(String aswVersion) {
		this.aswVersion = aswVersion;
	}

	public String getFrameworkVersion() {
		return frameworkVersion;
	}
	public void setFrameworkVersion(String frameworkVersion) {
		this.frameworkVersion = frameworkVersion;
	}

	public String getBswVersion() {
		return bswVersion;
	}
	public void setBswVersion(String bswVersion) {
		this.bswVersion = bswVersion;
	}

	public String getSimiccid() {
		return simiccid;
	}
	public void setSimiccid(String simiccid) {
		this.simiccid = simiccid;
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
		DeviceConfiguration configuration = (DeviceConfiguration) o;
		return (id == configuration.id &&
				Objects.equals(timestamp, configuration.timestamp) &&
				Objects.equals(url, configuration.url) &&
				Objects.equals(aswVersion, configuration.aswVersion) &&
				Objects.equals(frameworkVersion, configuration.frameworkVersion) &&
				Objects.equals(bswVersion, configuration.bswVersion) &&
				Objects.equals(simiccid, configuration.simiccid));
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, timestamp, url, aswVersion, frameworkVersion, bswVersion, simiccid);
	}
	
}
