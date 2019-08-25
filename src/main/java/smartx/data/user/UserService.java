package smartx.data.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public User addUser(User user) {
		return userRepository.save(user);
	}

	public User updateUser(User user) {
		return userRepository.save(user);
	}
	
	public Boolean deleteUser(int id) {
		Boolean deletedSuccessfully = false;
		if (userRepository.existsById(id)) {
			userRepository.deleteById(id);
			deletedSuccessfully = true;
		}
		return deletedSuccessfully;
	}

	public User getUserById(int id) {
		return userRepository.findById(id).get();
	}

	public User getUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public List<User> getUsersByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public List<User> getAllUsers() {
		List<User> users = new ArrayList<>();
		userRepository.findAll().forEach(users::add);
		return users;
	}
	
}
