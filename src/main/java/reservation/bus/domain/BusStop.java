package reservation.bus.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * @author Yuriy Tumakha
 */
@Data
@Entity
@Table(name = "bus_stop")
public class BusStop {

  @Id
  @GeneratedValue
  private Long id;

  @Column(unique = true, nullable = false)
  private String name;

  private Double latitude;
  private Double longitude;

}
