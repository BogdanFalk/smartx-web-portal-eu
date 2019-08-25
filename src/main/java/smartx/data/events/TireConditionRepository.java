package smartx.data.events;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface TireConditionRepository extends CrudRepository<TireCondition, Integer> {

	public List<TireCondition> findByVehicleId(int vehicleId);
	
	public List<TireCondition> findByVehicleIdOrderByTimestampDesc(int vehicleId);

	public TireCondition findFirstByVehicleIdOrderByTimestampDesc(int vehicleId);
	
}
