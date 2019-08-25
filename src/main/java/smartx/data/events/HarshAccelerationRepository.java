package smartx.data.events;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface HarshAccelerationRepository extends CrudRepository<HarshAcceleration, Integer> {

	public List<HarshAcceleration> findByVehicleId(int vehicleId);

	public List<HarshAcceleration> findByVehicleIdOrderByTimestampDesc(int vehicleId);

	public HarshAcceleration findFirstByVehicleIdOrderByTimestampDesc(int vehicleId);
	
}
