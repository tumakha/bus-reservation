package reservation.bus.importer.model;

import lombok.Data;

import java.time.LocalTime;

/**
 * @author Yuriy Tumakha
 */
@Data
public class BusStopTime {

  private String name;
  private LocalTime time;

}
