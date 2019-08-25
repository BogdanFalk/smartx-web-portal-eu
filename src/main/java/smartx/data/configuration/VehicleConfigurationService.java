package smartx.data.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class VehicleConfigurationService {

	@Autowired
	private VehicleConfigurationRepository vehicleConfigurationRepository;
	
	public VehicleConfiguration addVehicleConfiguration(VehicleConfiguration vehicleConfiguration) {
        return vehicleConfigurationRepository.save(vehicleConfiguration);
    }

    public VehicleConfiguration updateVehicleConfiguration(VehicleConfiguration vehicleConfiguration) {
        return vehicleConfigurationRepository.save(vehicleConfiguration);
    }
    
    public Boolean deleteVehicleConfiguration(int id) {
    	Boolean deletedSuccessfully = false;
    	if (vehicleConfigurationRepository.existsById(id)) {
    		vehicleConfigurationRepository.deleteById(id);
            deletedSuccessfully = true;
        }
        return deletedSuccessfully;
    }
    
    public void deleteAllVehicleConfigurationsForVehicle(int id) {
    	List<VehicleConfiguration> allVehicleConfigurations = getAllVehicleConfigurationsUnorderedByVehicleId(id);
    	for (VehicleConfiguration vehicleConfiguration : allVehicleConfigurations) {
    		vehicleConfigurationRepository.deleteById(vehicleConfiguration.getId());
		}
    }

    public VehicleConfiguration getVehicleConfigurationById(int id) {
        return vehicleConfigurationRepository.findById(id).get();
    }
    
    public List<VehicleConfiguration> getAllVehicleConfigurationsUnorderedByVehicleId(int vehicleId) {
		return vehicleConfigurationRepository.findByVehicleId(vehicleId);
	}
	
	public List<VehicleConfiguration> getAllVehicleConfigurationsOrderedDescendingByVehicleId(int vehicleId) {
		return vehicleConfigurationRepository.findByVehicleIdOrderByTimestampDesc(vehicleId);
	}
	
	public VehicleConfiguration getMostRecentVehicleConfigurationByVehicleId(int vehicleId) {
		return vehicleConfigurationRepository.findFirstByVehicleIdOrderByTimestampDesc(vehicleId);
	}
    
    public List<VehicleConfiguration> getAllVehicleConfigurations() {
    	List<VehicleConfiguration> vehicleConfigurations = new ArrayList<>();
    	vehicleConfigurationRepository.findAll().forEach(vehicleConfigurations::add);
		return vehicleConfigurations;
    }
	
}
