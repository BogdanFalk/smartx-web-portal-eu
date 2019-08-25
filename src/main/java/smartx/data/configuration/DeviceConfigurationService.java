package smartx.data.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DeviceConfigurationService {

	@Autowired
	private DeviceConfigurationRepository deviceConfigurationRepository;
	
	public DeviceConfiguration addDeviceConfiguration(DeviceConfiguration deviceConfiguration) {
        return deviceConfigurationRepository.save(deviceConfiguration);
    }

    public DeviceConfiguration updateDeviceConfiguration(DeviceConfiguration deviceConfiguration) {
        return deviceConfigurationRepository.save(deviceConfiguration);
    }
    
    public Boolean deleteDeviceConfiguration(int id) {
    	Boolean deletedSuccessfully = false;
    	if (deviceConfigurationRepository.existsById(id)) {
    		deviceConfigurationRepository.deleteById(id);
            deletedSuccessfully = true;
        }
        return deletedSuccessfully;
    }
    
    public void deleteAllDeviceConfigurationsForVehicle(int id) {
    	List<DeviceConfiguration> allDeviceConfigurations = getAllDeviceConfigurationsUnorderedByVehicleId(id);
    	for (DeviceConfiguration deviceConfiguration : allDeviceConfigurations) {
    		deviceConfigurationRepository.deleteById(deviceConfiguration.getId());
		}
    }

    public DeviceConfiguration getDeviceConfigurationById(int id) {
        return deviceConfigurationRepository.findById(id).get();
    }
    
    public List<DeviceConfiguration> getAllDeviceConfigurationsUnorderedByVehicleId(int vehicleId) {
		return deviceConfigurationRepository.findByVehicleId(vehicleId);
	}
	
	public List<DeviceConfiguration> getAllDeviceConfigurationsOrderedDescendingByVehicleId(int vehicleId) {
		return deviceConfigurationRepository.findByVehicleIdOrderByTimestampDesc(vehicleId);
	}
	
	public DeviceConfiguration getMostRecentDeviceConfigurationByVehicleId(int vehicleId) {
		return deviceConfigurationRepository.findFirstByVehicleIdOrderByTimestampDesc(vehicleId);
	}
    
    public List<DeviceConfiguration> getAllDeviceConfigurations() {
    	List<DeviceConfiguration> deviceConfigurations = new ArrayList<>();
    	deviceConfigurationRepository.findAll().forEach(deviceConfigurations::add);
		return deviceConfigurations;
    }
	
}
