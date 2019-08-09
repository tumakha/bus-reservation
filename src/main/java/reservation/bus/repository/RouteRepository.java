package reservation.bus.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import reservation.bus.domain.Route;

/**
 * @author Yuriy Tumakha
 */
@Repository
public interface RouteRepository extends PagingAndSortingRepository<Route, String> {
}
