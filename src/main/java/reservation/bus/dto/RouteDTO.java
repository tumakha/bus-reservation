package reservation.bus.dto;

import lombok.Value;
import reservation.bus.domain.Route;

import java.util.List;

/**
 * @author Yuriy Tumakha
 */
@Value
public class RouteDTO {

  private String name;

  private List<BusStopDTO> busStops;

  public static RouteDTO of(Route route, List<BusStopDTO> busStops) {
    return new RouteDTO(route.getName(), busStops);
  }

}
