package reservation.bus.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Yuriy Tumakha
 */
@Data
@Entity
@Table(name = "route")
public class Route {

  @Id
  private String name;

}
