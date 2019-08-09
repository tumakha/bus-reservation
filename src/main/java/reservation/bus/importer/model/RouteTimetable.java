package reservation.bus.importer.model;

import lombok.Data;

import java.util.List;

/**
 * @author Yuriy Tumakha
 */
@Data
public class RouteTimetable {

  private String name;
  private List<BusStopTime> busStops;

}
