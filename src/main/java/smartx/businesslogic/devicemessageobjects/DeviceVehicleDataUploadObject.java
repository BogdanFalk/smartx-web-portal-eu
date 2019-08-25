package smartx.businesslogic.devicemessageobjects;

import java.util.List;

import smartx.data.events.BorderCrossing;
import smartx.data.events.Dtc;
import smartx.data.events.EngineStatus;
import smartx.data.events.FuelConsumption;
import smartx.data.events.HarshAcceleration;
import smartx.data.events.HarshBreak;
import smartx.data.events.OverRpm;
import smartx.data.events.OverSpeed;
import smartx.data.events.TireCondition;
import smartx.data.events.VehicleTracking;

public class DeviceVehicleDataUploadObject extends DeviceMessageObject {

	private String serialNumber;
	private List<VehicleTracking> vehicleTrackingList;
	private List<EngineStatus> engineStatusList;
	private List<TireCondition> tireConditionList;
	private List<FuelConsumption> fuelConsumptionList;
	private List <Dtc> dtcList;
	private List <OverSpeed> overSpeedList;
	private List <OverRpm> overRpmList;
	private List <HarshAcceleration> harshAccelerationList;
	private List <HarshBreak> harshBreakList;
	private List <BorderCrossing> borderCrossingList;
	private long odometer;
	private int speed;
	private long engineHours;
	
	public DeviceVehicleDataUploadObject() {
		this.serialNumber = "";
	}

	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public List<VehicleTracking> getVehicleTrackingList() {
		return vehicleTrackingList;
	}
	public void setVehicleTrackingList(List<VehicleTracking> vehicleTrackingList) {
		this.vehicleTrackingList = vehicleTrackingList;
	}

	public List<EngineStatus> getEngineStatusList() {
		return engineStatusList;
	}
	public void setEngineStatusList(List<EngineStatus> engineStatusList) {
		this.engineStatusList = engineStatusList;
	}

	public List<TireCondition> getTireConditionList() {
		return tireConditionList;
	}
	public void setTireConditionList(List<TireCondition> tireConditionList) {
		this.tireConditionList = tireConditionList;
	}

	public List<FuelConsumption> getFuelConsumptionList() {
		return fuelConsumptionList;
	}
	public void setFuelConsumptionList(List<FuelConsumption> fuelConsumptionList) {
		this.fuelConsumptionList = fuelConsumptionList;
	}

	public List<Dtc> getDtcList() {
		return dtcList;
	}
	public void setDtcList(List<Dtc> dtcList) {
		this.dtcList = dtcList;
	}

	public List<OverSpeed> getOverSpeedList() {
		return overSpeedList;
	}
	public void setOverSpeedList(List<OverSpeed> overSpeedList) {
		this.overSpeedList = overSpeedList;
	}

	public List<OverRpm> getOverRpmList() {
		return overRpmList;
	}
	public void setOverRpmList(List<OverRpm> overRpmList) {
		this.overRpmList = overRpmList;
	}

	public List<HarshAcceleration> getHarshAccelerationList() {
		return harshAccelerationList;
	}
	public void setHarshAccelerationList(List<HarshAcceleration> harshAccelerationList) {
		this.harshAccelerationList = harshAccelerationList;
	}

	public List<HarshBreak> getHarshBreakList() {
		return harshBreakList;
	}
	public void setHarshBreakList(List<HarshBreak> harshBreakList) {
		this.harshBreakList = harshBreakList;
	}

	public List<BorderCrossing> getBorderCrossingList() {
		return borderCrossingList;
	}
	public void setBorderCrossingList(List<BorderCrossing> borderCrossingList) {
		this.borderCrossingList = borderCrossingList;
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
	
}
