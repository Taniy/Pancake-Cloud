package pancakes.data;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import pancakes.Order;
import pancakes.User;

import java.util.List;

public interface OrderRepository
         extends CrudRepository<Order, Long> {
    List<Order> findByUserOrderByPlacedAtDesc(User user, Pageable pageable);

}
