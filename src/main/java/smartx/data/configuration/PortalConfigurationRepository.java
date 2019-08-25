package smartx.data.configuration;

import org.springframework.data.repository.CrudRepository;

public interface PortalConfigurationRepository extends CrudRepository<PortalConfiguration, Integer> {

	public PortalConfiguration findById(int id);
	
}
