package smartx.data.vehicle;

import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface VehicleRepository extends CrudRepository<Vehicle, Integer> {

    public Vehicle findByDeviceSerialNumber(String deviceSerialNumber);
	
    public List<Vehicle> findByStatus(String status);

    public List<Vehicle> findByUserId(int userId);
    
    public List<Vehicle> findByUserUsername(String userUsername);
    
    public List<Vehicle> findByUserIdAndStatus(int userId, String status);

}