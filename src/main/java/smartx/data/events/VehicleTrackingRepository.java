package smartx.data.events;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface VehicleTrackingRepository extends CrudRepository<VehicleTracking, Integer> {
	
	public List<VehicleTracking> findByVehicleId(int vehicleId);
	
	public List<VehicleTracking> findByVehicleIdOrderByTimestampDesc(int vehicleId);

	public VehicleTracking findFirstByVehicleIdOrderByTimestampDesc(int vehicleId);
	
}
