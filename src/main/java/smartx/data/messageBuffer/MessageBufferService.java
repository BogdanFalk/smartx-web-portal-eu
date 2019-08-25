package smartx.data.messageBuffer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageBufferService {

	public MessageBufferService() {
	}

	@Autowired 
	private MessageBufferRepository messageBufferRepository;

	public MessageBuffer addMessageToBuffer(String deviceSerialNumber, String message, int sequenceNumber) {
		MessageBuffer messageEntity = new MessageBuffer();
		messageEntity.setDeviceSerialNumber(deviceSerialNumber);
		messageEntity.setMessage(message);
		messageEntity.setSequenceNumber(sequenceNumber);
		return messageBufferRepository.save(messageEntity);
	}

	public void removeMessageFromBuffer(int sequenceNumber) {
		MessageBuffer msg = messageBufferRepository.findBySequenceNumber(sequenceNumber);
		if(msg != null)
		{
			messageBufferRepository.deleteById(msg.getId());
		}
	}

	public List<MessageBuffer> getAllBufferedMessagesForDevice(String deviceSerialNumber)
	{
		return messageBufferRepository.findByDeviceSerialNumberOrderByIdAsc(deviceSerialNumber);
	}
}
