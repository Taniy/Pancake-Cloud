package pancakes.data;

import org.springframework.data.repository.CrudRepository;
import pancakes.Pancake;
import pancakes.User;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);

}
