package smartx.data.events;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface FuelConsumptionRepository extends CrudRepository<FuelConsumption, Integer> {

	public List<FuelConsumption> findByVehicleId(int vehicleId);

	public List<FuelConsumption> findByVehicleIdOrderByTimestampDesc(int vehicleId);

	public FuelConsumption findFirstByVehicleIdOrderByTimestampDesc(int vehicleId);
	
}
