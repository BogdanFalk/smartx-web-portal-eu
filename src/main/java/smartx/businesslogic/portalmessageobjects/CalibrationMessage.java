package smartx.businesslogic.portalmessageobjects;

import java.util.Objects;

public class CalibrationMessage {

	private boolean dtc;
	private boolean fuelConsumption;
	private boolean harshAcceleration;
	private boolean harshBreak;
	private boolean overRpm;
	private boolean overSpeed;
	private boolean tireCondition;
	
	public CalibrationMessage() {
	}

	public boolean isDtc() {
		return dtc;
	}
	public void setDtc(boolean dtc) {
		this.dtc = dtc;
	}

	public boolean isFuelConsumption() {
		return fuelConsumption;
	}
	public void setFuelConsumption(boolean fuelConsumption) {
		this.fuelConsumption = fuelConsumption;
	}

	public boolean isHarshAcceleration() {
		return harshAcceleration;
	}
	public void setHarshAcceleration(boolean harshAcceleration) {
		this.harshAcceleration = harshAcceleration;
	}

	public boolean isHarshBreak() {
		return harshBreak;
	}
	public void setHarshBreak(boolean harshBreak) {
		this.harshBreak = harshBreak;
	}

	public boolean isOverRpm() {
		return overRpm;
	}
	public void setOverRpm(boolean overRpm) {
		this.overRpm = overRpm;
	}

	public boolean isOverSpeed() {
		return overSpeed;
	}
	public void setOverSpeed(boolean overSpeed) {
		this.overSpeed = overSpeed;
	}

	public boolean isTireCondition() {
		return tireCondition;
	}
	public void setTireCondition(boolean tireCondition) {
		this.tireCondition = tireCondition;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CalibrationMessage calibrationMessage = (CalibrationMessage) o;
		return (Objects.equals(dtc, calibrationMessage.dtc) &&
				Objects.equals(fuelConsumption, calibrationMessage.fuelConsumption) &&
				Objects.equals(harshAcceleration, calibrationMessage.harshAcceleration) &&
				Objects.equals(harshBreak, calibrationMessage.harshBreak) &&
				Objects.equals(overRpm, calibrationMessage.overRpm) &&
				Objects.equals(overSpeed, calibrationMessage.overSpeed) &&
				Objects.equals(tireCondition, calibrationMessage.tireCondition)
				);
	}
	
}
