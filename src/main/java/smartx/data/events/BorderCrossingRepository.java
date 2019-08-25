package smartx.data.events;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface BorderCrossingRepository extends CrudRepository<BorderCrossing, Integer> {

	public List<BorderCrossing> findByVehicleId(int vehicleId);

	public List<BorderCrossing> findByVehicleIdOrderByTimestampDesc(int vehicleId);

	public BorderCrossing findFirstByVehicleIdOrderByTimestampDesc(int vehicleId);
	
}
