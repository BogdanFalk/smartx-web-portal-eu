package smartx.data.driver;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface DriverRepository extends CrudRepository<Driver, Integer> {

	public Driver findByVehicleId(int vehicleId);

	public List<Driver> findByUserId(int userId);

	public List<Driver> findByUserUsername(String userUsername);

}
