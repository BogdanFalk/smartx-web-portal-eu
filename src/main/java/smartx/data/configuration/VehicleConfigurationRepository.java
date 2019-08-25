package smartx.data.configuration;

import java.util.List;

import org.springframework.data.repository.CrudRepository;


public interface VehicleConfigurationRepository extends CrudRepository<VehicleConfiguration, Integer> {

	public List<VehicleConfiguration> findByVehicleId(int vehicleId);

	public List<VehicleConfiguration> findByVehicleIdOrderByTimestampDesc(int vehicleId);

	public VehicleConfiguration findFirstByVehicleIdOrderByTimestampDesc(int vehicleId);

}
