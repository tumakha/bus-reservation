package reservation.bus.resource;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.hibernate.validator.constraints.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reservation.bus.domain.Route;
import reservation.bus.domain.Timetable;
import reservation.bus.dto.BusStopDTO;
import reservation.bus.dto.PageWrapper;
import reservation.bus.dto.RouteDTO;
import reservation.bus.repository.RouteRepository;
import reservation.bus.repository.TimetableRepository;

import javax.validation.constraints.Min;
import java.util.List;

import static java.lang.String.format;
import static java.net.HttpURLConnection.*;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.PageRequest.of;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * @author Yuriy Tumakha
 */
@RestController
@Validated
@RequestMapping(path = "/v1/routes", produces = APPLICATION_JSON_UTF8_VALUE)
public class RouteResource implements Resource {

  private static final Logger LOG = LoggerFactory.getLogger(RouteResource.class);

  @Autowired
  private RouteRepository routeRepository;

  @Autowired
  private TimetableRepository timetableRepository;

  @GetMapping
  @ApiOperation("Get all routes")
  public ResponseEntity<PageWrapper<RouteDTO>> getRoutes(

      @Min(0)
      @ApiParam("Zero-based page index")
      @RequestParam(name = "page", defaultValue = "0") Integer page,

      @Range(min = 1, max = 200)
      @ApiParam("Size of page to be returned")
      @RequestParam(name = "size", defaultValue = "50") Integer size) {

    return page(routeRepository.findAll(of(page, size, Sort.by("name"))).map(r -> RouteDTO.of(r, null)));
  }

  @GetMapping("{name}")
  @ApiOperation("Get route by name")
  @ApiResponses(value = @ApiResponse(code = HTTP_NOT_FOUND, message = "Route not found"))
  public ResponseEntity<RouteDTO> getRoute(@PathVariable(value = "name") String name) {

    Route route = routeRepository.findById(name)
        .orElseThrow(() -> errorResponse(NOT_FOUND, format("Route not found by name '%s'", name)));

    List<Timetable> timetable = timetableRepository.findByRouteOrderByPickupTime(route);
    List<BusStopDTO> busStops = timetable.stream().map(BusStopDTO::of).collect(toList());

    return ResponseEntity.ok(RouteDTO.of(route, busStops));
  }

}
