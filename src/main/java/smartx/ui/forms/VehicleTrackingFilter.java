package smartx.ui.forms;

import java.time.LocalDate;
import java.util.Objects;
import java.util.TimeZone;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * This class is used in the vehicle tracking page`s filter form.
 * @author uidw6860
 *
 */
public class VehicleTrackingFilter {
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;
	
	/**
	 * 
	 */
	public VehicleTrackingFilter() {
		// LocalDateTime.now() uses the system default (RO) zone ID, so we must specify to use GMT zone ID
		this.date = LocalDate.now(TimeZone.getTimeZone("GMT").toZoneId());
	}

	/**
	 * @param mDate
	 */
	public VehicleTrackingFilter(LocalDate date) {
		super();
		this.date = date;
	}

	/**
	 * Getter for the date.
	 * @return The date in human readable format.
	 */
	public LocalDate getDate() {
		return date;
	}

	/**
	 * Setter for the date.
	 * @param date: the date in human readable format.
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VehicleTrackingFilter vehicle = (VehicleTrackingFilter) o;
		return (date == vehicle.date);
	}

	@Override
	public int hashCode() {
		return Objects.hash(date);
	}
}
