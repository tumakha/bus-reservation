package reservation.bus.dto;

import lombok.Value;
import reservation.bus.domain.BusStop;
import reservation.bus.domain.Timetable;

import java.time.LocalTime;
import java.util.List;

/**
 * @author Yuriy Tumakha
 */
@Value
public class BusStopReservationDTO {

  private String name;
  private LocalTime pickupTime;
  private List<ReservationDTO> reservations;

  public static BusStopReservationDTO of(Timetable t, List<ReservationDTO> reservations) {
    BusStop bs = t.getBusStop();
    return new BusStopReservationDTO(bs.getName(), t.getPickupTime(), reservations);
  }

}
