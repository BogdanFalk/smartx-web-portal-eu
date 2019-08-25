package smartx.data.events;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface DtcRepository extends CrudRepository<Dtc, Integer> {

	public List<Dtc> findByVehicleId(int vehicleId);

	public List<Dtc> findByVehicleIdOrderByTimestampDesc(int vehicleId);

	public Dtc findFirstByVehicleIdOrderByTimestampDesc(int vehicleId);
	
}
