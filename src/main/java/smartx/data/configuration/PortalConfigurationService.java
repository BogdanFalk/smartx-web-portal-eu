package smartx.data.configuration;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PortalConfigurationService {

	private PortalConfiguration initialPortalConfiguration;

	@Autowired
	PortalConfigurationRepository portalConfigurationRepository;

	public PortalConfigurationService() {
		initialPortalConfiguration = new PortalConfiguration();
		initialPortalConfiguration.setCurrentMessageSequenceNumber(0);
	}
	
	@PostConstruct
	public void init(){
		PortalConfiguration singlePortalConfigurationRecord = portalConfigurationRepository.findById(1);
		if(singlePortalConfigurationRecord == null)
		{
			initialPortalConfiguration = portalConfigurationRepository.save(initialPortalConfiguration);
		}
		else
		{
			initialPortalConfiguration = singlePortalConfigurationRecord;
		}
	}

	public int getCurrentSequenceNumber()
	{
		return initialPortalConfiguration.getCurrentMessageSequenceNumber();
	}

	public int incrementAndRetrieveMessageSequenceNumber()
	{
		int newSequenceNumber = 1;

		// the table should contain a single entry, added from the very start, in the constructor of the service
		// this record should only be updated afterwards

		// if we previously sent any messages, increment by 1 the sequence number
		System.out.println("Current seq nr: " + initialPortalConfiguration.getCurrentMessageSequenceNumber());
		if(initialPortalConfiguration.getCurrentMessageSequenceNumber() != 0)
		{
			newSequenceNumber = initialPortalConfiguration.getCurrentMessageSequenceNumber() + 1;
			// do not exceed 65535, start over from 1
			if(newSequenceNumber >= 65535)
			{
				newSequenceNumber = 1;
			}
		}

		initialPortalConfiguration.setCurrentMessageSequenceNumber(newSequenceNumber);
		portalConfigurationRepository.save(initialPortalConfiguration);
		System.out.println("New seq nr: " + newSequenceNumber);
		return newSequenceNumber;
	}
}
