package reservation.bus.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

/**
 * @author Yuriy Tumakha
 */
@Data
@Entity
@Table(
    name = "timetable",
    uniqueConstraints = @UniqueConstraint(columnNames = {"route_name", "bus_stop_id"})
)
public class Timetable {

  @Id
  @GeneratedValue
  private Long id;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "route_name", updatable = false, nullable = false)
  private Route route;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "bus_stop_id", updatable = false, nullable = false)
  private BusStop busStop;

  @NotNull
  @Column( nullable = false)
  private LocalTime pickupTime;

}
