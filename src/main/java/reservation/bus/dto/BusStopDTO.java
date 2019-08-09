package reservation.bus.dto;

import lombok.Value;
import reservation.bus.domain.BusStop;
import reservation.bus.domain.Timetable;

import java.time.LocalTime;

/**
 * @author Yuriy Tumakha
 */
@Value
public class BusStopDTO {

  private Long id;
  private String name;
  private Double latitude;
  private Double longitude;
  private LocalTime pickupTime;

  public static BusStopDTO of(Timetable t) {
    BusStop bs = t.getBusStop();
    return new BusStopDTO(bs.getId(), bs.getName(), bs.getLatitude(), bs.getLongitude(), t.getPickupTime());
  }

}
