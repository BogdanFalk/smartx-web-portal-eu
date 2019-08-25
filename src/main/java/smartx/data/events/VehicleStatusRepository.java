package smartx.data.events;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface VehicleStatusRepository extends CrudRepository<VehicleStatus, Integer> {

	public List<VehicleStatus> findByVehicleId(int vehicleId);

	public List<VehicleStatus> findByVehicleIdOrderByTimestampDesc(int vehicleId);

	public VehicleStatus findFirstByVehicleIdOrderByTimestampDesc(int vehicleId);

}
