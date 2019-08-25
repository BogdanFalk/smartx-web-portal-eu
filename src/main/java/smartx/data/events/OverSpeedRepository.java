package smartx.data.events;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface OverSpeedRepository extends CrudRepository<OverSpeed, Integer> {

	public List<OverSpeed> findByVehicleId(int vehicleId);

	public List<OverSpeed> findByVehicleIdOrderByTimestampDesc(int vehicleId);

	public OverSpeed findFirstByVehicleIdOrderByTimestampDesc(int vehicleId);
	
}
