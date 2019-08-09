package reservation.bus.dto;

import lombok.Value;
import reservation.bus.domain.Reservation;

/**
 * @author Yuriy Tumakha
 */
@Value
public class ReservationDTO {

  private Long reservationId;
  private String busStop;
  private String childName;

  public static ReservationDTO of(Reservation r) {
    return new ReservationDTO(r.getId(), r.getTimetable().getBusStop().getName(), r.getChildName());
  }

  public static ReservationDTO withoutBusStop(Reservation r) {
    return new ReservationDTO(r.getId(), null, r.getChildName());
  }

}
