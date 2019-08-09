package reservation.bus.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Yuriy Tumakha
 */
@Data
public class CreateReservationDTO {

  @NotNull
  private String busStop;

  @NotNull
  private String childName;

}
