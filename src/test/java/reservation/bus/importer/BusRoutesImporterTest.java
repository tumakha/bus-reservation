package reservation.bus.importer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;
import reservation.bus.domain.BusStop;
import reservation.bus.domain.Route;
import reservation.bus.domain.Timetable;
import reservation.bus.repository.BusStopRepository;
import reservation.bus.repository.RouteRepository;
import reservation.bus.repository.TimetableRepository;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Yuriy Tumakha
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BusRoutesImporterTest implements ResourceReader {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Autowired
  private BusRoutesImporter busRoutesImporter;

  @Autowired
  private BusStopRepository busStopRepository;

  @Autowired
  private RouteRepository routeRepository;

  @Autowired
  private TimetableRepository timetableRepository;

  @Value("classpath:import/bus-stops-test.json")
  private Resource busStopsJson;

  @Value("classpath:import/routes-test.json")
  private Resource routesJson;

  @Value("classpath:route-stops-duplication.json")
  private Resource routeStopsDuplicationJson;

  @Test
  public void checkDataLoadedByTestDataImporter() {
    BusStop busStop = busStopRepository.findByName("stop1");
    assertThat(busStop, notNullValue());
    assertThat(busStop.getLatitude(), equalTo(51.513313));

    Optional<Route> route2 = routeRepository.findById("route2");
    assertThat(route2.isPresent(), is(true));

    List<Timetable> timetableList = route2.map(timetableRepository::findByRouteOrderByPickupTime).orElse(emptyList());
    assertThat(timetableList, hasSize(3));

    Timetable timetable1 = timetableList.get(0);
    assertThat(timetable1.getBusStop().getName(), equalTo("stop4"));
    assertThat(timetable1.getPickupTime(), equalTo(LocalTime.parse("14:10")));
  }

  @Test(expected = DataIntegrityViolationException.class)
  public void testLoadDuplicatedBusStops() throws IOException {
    busRoutesImporter.loadBusStops(getResourceAsString(busStopsJson));
  }

  @Test(expected = DataIntegrityViolationException.class)
  public void testLoadDuplicatedRoutes() throws IOException {
    busRoutesImporter.loadRoutes(getResourceAsString(routesJson));
  }

  @Test
  public void testLoadRouteWith2TheSameStops() throws IOException {
    expectedEx.expect(DataIntegrityViolationException.class);
    expectedEx.expectMessage(
        allOf(containsString("ConstraintViolationException: could not execute statement"),
            containsString("insert into timetable")));

    busRoutesImporter.loadRoutes(getResourceAsString(routeStopsDuplicationJson));
  }

}