package smartx.data.events;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface EngineStatusRepository extends CrudRepository<EngineStatus, Integer> {

	public List<EngineStatus> findByVehicleId(int vehicleId);

	public List<EngineStatus> findByVehicleIdOrderByTimestampDesc(int vehicleId);

	public EngineStatus findFirstByVehicleIdOrderByTimestampDesc(int vehicleId);
	
}
