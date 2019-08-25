package smartx.businesslogic.portalmessageobjects;

import smartx.businesslogic.MessageType;

public class PortalMessageObject {

	private int sequenceNumber;
	private String deviceSerialNumber;
	private MessageType messageType;
	private String jsonMessage;
	
	public PortalMessageObject() {
		this.deviceSerialNumber = "";
		this.jsonMessage = "";
	}
	
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
	public String getDeviceSerialNumber() {
		return deviceSerialNumber;
	}
	public void setDeviceSerialNumber(String deviceSerialNumber) {
		this.deviceSerialNumber = deviceSerialNumber;
	}
	
	public MessageType getMessageType() {
		return messageType;
	}
	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}
	
	public String getJsonMessage() {
		return jsonMessage;
	}
	public void setJsonMessage(String jsonMessage) {
		this.jsonMessage = jsonMessage;
	}
	
}
