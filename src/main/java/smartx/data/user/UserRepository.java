package smartx.data.user;

import java.util.List;

import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<User, Integer> {

	public User findByUsername(String username);

	public List<User> findByEmail(String email);
}