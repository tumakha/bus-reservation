package reservation.bus.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author Yuriy Tumakha
 */
@Data
@Entity
@Table(
    name = "reservation",
    uniqueConstraints = @UniqueConstraint(columnNames = {"timetable_id", "date", "childName"})
)
public class Reservation {

  @Id
  @GeneratedValue
  private Long id;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "timetable_id", nullable = false)
  private Timetable timetable;

  @NotNull
  @Column(nullable = false)
  private LocalDate date;

  @NotNull
  @Column(nullable = false)
  private String childName;

}
