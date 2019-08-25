package smartx.data.events;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface HarshBreakRepository extends CrudRepository<HarshBreak, Integer> {

	public List<HarshBreak> findByVehicleId(int vehicleId);

	public List<HarshBreak> findByVehicleIdOrderByTimestampDesc(int vehicleId);

	public HarshBreak findFirstByVehicleIdOrderByTimestampDesc(int vehicleId);
	
}
