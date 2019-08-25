package smartx.data.messageBuffer;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface MessageBufferRepository  extends CrudRepository<MessageBuffer, Integer> {

	public MessageBuffer findBySequenceNumber(int sequenceNumber);
	
	public MessageBuffer findFirstByOrderBySequenceNumberDesc();
	
	public List <MessageBuffer> findByDeviceSerialNumberOrderByIdAsc(String deviceSerialNumber);
	
}
