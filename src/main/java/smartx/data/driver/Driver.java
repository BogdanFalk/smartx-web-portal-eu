package smartx.data.driver;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import smartx.data.user.User;
import smartx.data.vehicle.Vehicle;

@Entity()
public class Driver {

	@Id
	@GeneratedValue
	@Column(name = "id", unique = true)
	private int id;

	private String driverUsername;
	private String fullName;
	private String licenseNumber;
	private String address;
	private String phoneNumber;
	@Lob
    private byte[] picture;
	
	@ManyToOne
	@JoinColumn
	private User user;
	
	@OneToOne(mappedBy = "driver")
	@JoinColumn
	private Vehicle vehicle;

	public Driver() {
		this.driverUsername = "";
		this.fullName = "";
		this.licenseNumber = "";
		this.address = "";
		this.phoneNumber = "";
		this.user = new User();
		this.vehicle = null;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getDriverUsername() {
		return driverUsername;
	}
	public void setDriverUsername(String username) {
		this.driverUsername = username;
	}

	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}
	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public byte[] getPicture() {
		return picture;
	}
	public void setPicture(byte[] picture) {
		this.picture = picture;
	}

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
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
		Driver driver = (Driver) o;
		return (id == driver.id //&&
//				Objects.equals(driverUsername, driver.username) &&
//				Objects.equals(fullName, driver.fullName) &&
//				Objects.equals(licenseNumber, driver.licenseNumber) &&
//				Objects.equals(address, driver.address) &&
//				Objects.equals(phoneNumber, driver.phoneNumber)
				);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, driverUsername, fullName, licenseNumber, address, 
				phoneNumber);
	}
	
}
