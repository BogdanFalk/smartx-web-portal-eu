package smartx.businesslogic;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum MessageType {

	eMessageType_DeviceInitialConfiguration,
	eMessageType_DeviceConfigurationFinished,
	eMessageType_DeviceSleep,
	eMessageType_DeviceVehicleDataUpload,
	eMessageType_DeviceExtractLogsResponse,
	eMessageType_PortalConfiguration,
	eMessageType_PortalVehicleStatusRequest,
	eMessageType_PortalExtractLogs
}
