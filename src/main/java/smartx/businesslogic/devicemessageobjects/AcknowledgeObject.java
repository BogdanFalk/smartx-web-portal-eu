package smartx.businesslogic.devicemessageobjects;

public class AcknowledgeObject {

	private int acknowledgeSequenceNumber;

	public AcknowledgeObject() {
	}

	public AcknowledgeObject(int acknowledgeSequenceNumber) {
		this.acknowledgeSequenceNumber = acknowledgeSequenceNumber;
	}

	public int getAcknowledgeSequenceNumber() {
		return acknowledgeSequenceNumber;
	}

	public void setAcknowledgeSequenceNumber(int acknowledgeSequenceNumber) {
		this.acknowledgeSequenceNumber = acknowledgeSequenceNumber;
	}

	@Override
    public String toString() {
        return "{\"acknowledgeSequenceNumber\": " + acknowledgeSequenceNumber + "}";
    }

}
