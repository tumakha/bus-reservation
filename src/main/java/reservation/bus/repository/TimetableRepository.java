package reservation.bus.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reservation.bus.domain.Route;
import reservation.bus.domain.Timetable;

import java.util.List;
import java.util.Optional;

/**
 * @author Yuriy Tumakha
 */
@Repository
public interface TimetableRepository extends PagingAndSortingRepository<Timetable, Long> {

  @Transactional(readOnly = true)
  List<Timetable> findByRouteOrderByPickupTime(Route route);

  @Transactional(readOnly = true)
  @Query("SELECT t FROM Timetable t WHERE t.route = :route AND t.busStop.name = :busStop")
  Optional<Timetable> findByRouteAndBusStop(
      @Param("route") Route route,
      @Param("busStop") String busStop);

}
