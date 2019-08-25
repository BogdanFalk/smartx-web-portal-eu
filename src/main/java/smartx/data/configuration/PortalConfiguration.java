package smartx.data.configuration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class PortalConfiguration {

	public PortalConfiguration() {
	}
	
	@Id
	@GeneratedValue
	@Column(unique = true)
	private int id;
	
	private int currentMessageSequenceNumber;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getCurrentMessageSequenceNumber() {
		return currentMessageSequenceNumber;
	}
	public void setCurrentMessageSequenceNumber(int currentMessageSequenceNumber) {
		this.currentMessageSequenceNumber = currentMessageSequenceNumber;
	}
	
}
