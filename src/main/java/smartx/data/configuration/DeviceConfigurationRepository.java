package smartx.data.configuration;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface DeviceConfigurationRepository extends CrudRepository<DeviceConfiguration, Integer> {

	public List<DeviceConfiguration> findByVehicleId(int vehicleId);
	
	public List<DeviceConfiguration> findByVehicleIdOrderByTimestampDesc(int vehicleId);

	public DeviceConfiguration findFirstByVehicleIdOrderByTimestampDesc(int vehicleId);
	
}
