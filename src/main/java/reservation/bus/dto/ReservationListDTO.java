package reservation.bus.dto;

import lombok.Value;

import java.util.List;

/**
 * @author Yuriy Tumakha
 */
@Value
public class ReservationListDTO {

  private List<BusStopReservationDTO> busStops;

}
