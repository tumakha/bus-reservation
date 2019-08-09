package reservation.bus.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reservation.bus.domain.Reservation;
import reservation.bus.domain.Route;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Yuriy Tumakha
 */
@Repository
public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long> {

  @Transactional(readOnly = true)
  @Query("SELECT r FROM Reservation r WHERE r.timetable.route = :route AND r.date = :date")
  List<Reservation> findByRouteAndDate(
      @Param("route") Route route,
      @Param("date") LocalDate date);

}
