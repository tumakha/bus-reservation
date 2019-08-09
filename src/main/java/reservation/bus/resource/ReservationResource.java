package reservation.bus.resource;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reservation.bus.domain.Reservation;
import reservation.bus.dto.CreateReservationDTO;
import reservation.bus.dto.ReservationDTO;
import reservation.bus.dto.ReservationListDTO;
import reservation.bus.dto.StatusDTO;
import reservation.bus.service.ReservationService;

import javax.validation.Valid;
import java.time.LocalDate;

import static java.net.HttpURLConnection.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.ResponseEntity.status;

/**
 * @author Yuriy Tumakha
 */
@RestController
@Validated
@RequestMapping(path = "/v1/reservations", produces = APPLICATION_JSON_UTF8_VALUE)
public class ReservationResource implements Resource {

  private static final Logger LOG = LoggerFactory.getLogger(ReservationResource.class);

  @Autowired
  private ReservationService reservationService;

  @GetMapping("{route_name}/{date}")
  @ApiOperation("Get reservations")
  @ApiResponses(value = {
      @ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad date format"),
      @ApiResponse(code = HTTP_NOT_FOUND, message = "Route not found")
  })
  public ResponseEntity<ReservationListDTO> getReservations(
      @PathVariable(value = "route_name") String routeName,
      @PathVariable(value = "date") @DateTimeFormat(iso = ISO.DATE) LocalDate date) {

    ReservationListDTO reservations = reservationService.getReservations(routeName, date);

    return ResponseEntity.ok(reservations);
  }

  @PostMapping("{route_name}/{date}")
  @ApiOperation("Create reservation")
  @ApiResponses(value = {
      @ApiResponse(code = HTTP_CREATED, message = "Reservation created"),
      @ApiResponse(code = HTTP_BAD_REQUEST, message = "Date is not a school day or bad parameters format"),
      @ApiResponse(code = HTTP_NOT_FOUND, message = "Route or Bus stop not found"),
      @ApiResponse(code = HTTP_CONFLICT, message = "Child already registered")
  })
  public ResponseEntity<ReservationDTO> createReservation(
      @PathVariable(value = "route_name") String routeName,
      @PathVariable(value = "date") @DateTimeFormat(iso = ISO.DATE) LocalDate date,
      @Valid @RequestBody CreateReservationDTO createReservationDTO) {

    ReservationDTO reservation = reservationService.createReservation(routeName, date, createReservationDTO);

    return status(CREATED).body(reservation);
  }

  @GetMapping("{route_name}/{date}/{reservation_id}")
  @ApiOperation("Get reservation by id")
  @ApiResponses(value = {
      @ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad date format"),
      @ApiResponse(code = HTTP_NOT_FOUND, message = "Route or Reservation not found")
  })
  public ResponseEntity<ReservationDTO> getReservation(
      @PathVariable(value = "route_name") String routeName,
      @PathVariable(value = "date") @DateTimeFormat(iso = ISO.DATE) LocalDate date,
      @PathVariable(value = "reservation_id") Long reservationId) {

    Reservation reservation = reservationService.getReservation(routeName, date, reservationId);

    return ResponseEntity.ok(ReservationDTO.of(reservation));
  }

  @PutMapping("{route_name}/{date}/{reservation_id}")
  @ApiOperation("Update reservation")
  @ApiResponses(value = {
      @ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad parameters format"),
      @ApiResponse(code = HTTP_NOT_FOUND, message = "Route or Reservation not found"),
      @ApiResponse(code = HTTP_CONFLICT, message = "Child already registered")
  })
  public ResponseEntity<ReservationDTO> updateReservation(
      @PathVariable(value = "route_name") String routeName,
      @PathVariable(value = "date") @DateTimeFormat(iso = ISO.DATE) LocalDate date,
      @PathVariable(value = "reservation_id") Long reservationId,
      @Valid @RequestBody CreateReservationDTO reservationDTO) {

    ReservationDTO reservation = reservationService.updateReservation(routeName, date, reservationId, reservationDTO);

    return ResponseEntity.ok(reservation);
  }

  @DeleteMapping("{route_name}/{date}/{reservation_id}")
  @ApiOperation("Delete reservation")
  @ApiResponses(value = {
      @ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad parameters format"),
      @ApiResponse(code = HTTP_NOT_FOUND, message = "Route or Reservation not found")
  })
  public ResponseEntity<StatusDTO> deleteReservation(
      @PathVariable(value = "route_name") String routeName,
      @PathVariable(value = "date") @DateTimeFormat(iso = ISO.DATE) LocalDate date,
      @PathVariable(value = "reservation_id") Long reservationId) {

    return ResponseEntity.ok(reservationService.deleteReservation(routeName, date, reservationId));
  }

}
