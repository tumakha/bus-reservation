package reservation.bus.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reservation.bus.domain.BusStop;

/**
 * @author Yuriy Tumakha
 */
@Repository
public interface BusStopRepository extends PagingAndSortingRepository<BusStop, String> {

  @Transactional(readOnly = true)
  BusStop findByName(String name);

}
