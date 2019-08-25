package smartx.data.events;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface OverRpmRepository extends CrudRepository<OverRpm, Integer> {

	public List<OverRpm> findByVehicleId(int vehicleId);

	public List<OverRpm> findByVehicleIdOrderByTimestampDesc(int vehicleId);

	public OverRpm findFirstByVehicleIdOrderByTimestampDesc(int vehicleId);
	
}
