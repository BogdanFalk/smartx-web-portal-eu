package smartx.businesslogic.vehicletracking;

public class GMapsCoordinates {
	private float latitude;
	private float longitude;
	private String status;

	public GMapsCoordinates() {
		this.status = "";
	}

	public GMapsCoordinates(float latitude, float longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.status = "";
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
