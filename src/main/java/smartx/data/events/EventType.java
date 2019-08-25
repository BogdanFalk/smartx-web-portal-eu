package smartx.data.events;

public enum EventType {
	BORDER_CROSSING("Border Crossing", 1), 
	DTC("DTC", 2), 
	ENGINE_STATUS("Engine Status", 3), 
	FUEL_CONSUMPTION("Fuel Consumption", 4),
	HARSH_ACCELERATION("Harsh Acceleration", 5),
	HARSH_BREAK("Harsh Break", 6),
	OVER_RPM("Over RPM", 7),
	OVER_SPEED("Over Speed", 8),
	TIRE_CONDITION("Tire Condition", 9),
	VEHICLE_TRACKING("Vehicle Tracking", 10);
	
	private final String typeName;
	private final int typeValue;

	EventType(final String name, final int value) {
		this.typeName = name;
		this.typeValue = value;
	}

	public final int getValue() {
		return typeValue;
	}
	
	public final String getName() {
		return typeName;
	}
}
