package reservation.bus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reservation.bus.domain.Reservation;
import reservation.bus.domain.Route;
import reservation.bus.domain.Timetable;
import reservation.bus.dto.*;
import reservation.bus.repository.ReservationRepository;
import reservation.bus.repository.RouteRepository;
import reservation.bus.repository.TimetableRepository;
import reservation.bus.resource.Resource;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.util.stream.Collectors.*;
import static org.springframework.http.HttpStatus.*;

/**
 * @author Yuriy Tumakha
 */
@Service
public class ReservationService implements Resource {

  @Autowired
  private RouteRepository routeRepository;

  @Autowired
  private TimetableRepository timetableRepository;

  @Autowired
  private ReservationRepository reservationRepository;

  @Transactional(readOnly = true)
  public ReservationListDTO getReservations(String routeName, LocalDate date) {
    Route route = getRoute(routeName);
    List<Timetable> timetable = timetableRepository.findByRouteOrderByPickupTime(route);
    List<Reservation> reservations = reservationRepository.findByRouteAndDate(route, date);

    Map<String, List<ReservationDTO>> stopReservations = reservations.stream()
        .collect(groupingBy(r -> r.getTimetable().getBusStop().getName(),
            mapping(ReservationDTO::withoutBusStop, toList())));

    List<BusStopReservationDTO> busStopsWithReservations = timetable.stream().map(t ->
        BusStopReservationDTO.of(t, stopReservations.get(t.getBusStop().getName())))
        .collect(toList());

    return new ReservationListDTO(busStopsWithReservations);
  }

  public ReservationDTO createReservation(String routeName, LocalDate date, CreateReservationDTO createReservationDTO) {
    checkDate(date);
    Route route = getRoute(routeName);
    String busStop = createReservationDTO.getBusStop();
    String childName = createReservationDTO.getChildName();

    Timetable timetable = timetableRepository.findByRouteAndBusStop(route, busStop)
        .orElseThrow(() -> errorResponse(NOT_FOUND, format("Unknown bus stop '%s' on route '%s'", busStop, routeName)));

    Reservation reservation = new Reservation();
    reservation.setTimetable(timetable);
    reservation.setDate(date);
    reservation.setChildName(childName);

    try {
      Reservation dbReservation = reservationRepository.save(reservation);
      return new ReservationDTO(dbReservation.getId(), busStop, childName);

    } catch (DataIntegrityViolationException ex) {
      throw errorResponse(CONFLICT, "Child already registered");
    }
  }

  public ReservationDTO updateReservation(String routeName, LocalDate date,
                                          Long reservationId, CreateReservationDTO reservationDTO) {
    String busStop = reservationDTO.getBusStop();
    String childName = reservationDTO.getChildName();

    Reservation reservation = getReservation(routeName, date, reservationId);
    reservation.setChildName(childName);
    if (!busStop.equals(reservation.getTimetable().getBusStop().getName())) {
      Timetable timetable = timetableRepository.findByRouteAndBusStop(getRoute(routeName), busStop)
          .orElseThrow(() -> errorResponse(NOT_FOUND, format("Unknown bus stop '%s' on route '%s'", busStop, routeName)));
      reservation.setTimetable(timetable);
    }

    try {
      Reservation dbReservation = reservationRepository.save(reservation);
      return new ReservationDTO(dbReservation.getId(), busStop, childName);

    } catch (DataIntegrityViolationException ex) {
      throw errorResponse(CONFLICT, "Child already registered");
    }
  }

  public StatusDTO deleteReservation(String routeName, LocalDate date, Long reservationId) {
    Reservation reservation = getReservation(routeName, date, reservationId);
    reservationRepository.delete(reservation);
    return new StatusDTO("OK");
  }

  public Reservation getReservation(String routeName, LocalDate date, Long reservationId) {
    Route route = getRoute(routeName);

    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> errorResponse(NOT_FOUND, format("Reservation not found by id = %d", reservationId)));

    String dbRouteName = reservation.getTimetable().getRoute().getName();

    if (!route.getName().equals(dbRouteName))
      throw errorResponse(BAD_REQUEST, format("Reservation route name '%s' is not matched with path", dbRouteName));

    if (!date.equals(reservation.getDate()))
      throw errorResponse(BAD_REQUEST, format("Reservation date '%s' is not matched with path", reservation.getDate()));

    return reservation;
  }

  private void checkDate(LocalDate date) {
    if (LocalDate.now().compareTo(date) > 0)
      throw errorResponse(BAD_REQUEST, date + " is in the past");

    DayOfWeek dayOfWeek = date.getDayOfWeek();
    if (dayOfWeek == SATURDAY || dayOfWeek == SUNDAY)
      throw errorResponse(BAD_REQUEST, date + " is not a school day");
  }

  private Route getRoute(String name) {
    return routeRepository.findById(name)
        .orElseThrow(() -> errorResponse(NOT_FOUND, format("Route not found by name '%s'", name)));
  }

}
