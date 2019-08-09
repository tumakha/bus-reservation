package reservation.bus.importer;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reservation.bus.domain.BusStop;
import reservation.bus.domain.Route;
import reservation.bus.domain.Timetable;
import reservation.bus.importer.model.BusStopTime;
import reservation.bus.importer.model.RouteTimetable;
import reservation.bus.repository.BusStopRepository;
import reservation.bus.repository.RouteRepository;
import reservation.bus.repository.TimetableRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

/**
 * @author Yuriy Tumakha
 */
@Component
public class BusRoutesImporter implements ResourceReader {

  @Autowired
  private BusStopRepository busStopRepository;

  @Autowired
  private RouteRepository routeRepository;

  @Autowired
  private TimetableRepository timetableRepository;

  @Autowired
  private JsonSupport jsonSupport;

  @Value("classpath:data/bus-stops.json")
  private Resource busStopsJson;

  @Value("classpath:data/routes.json")
  private Resource routesJson;

  @PostConstruct
  public void init() throws IOException {
    load(busStopsJson, routesJson);
  }

  public void load(Resource busStopsResource, Resource routesResource) throws IOException {
    loadBusStops(getResourceAsString(busStopsResource));
    loadRoutes(getResourceAsString(routesResource));
  }

  @Transactional
  public void loadBusStops(String json) throws IOException {
    List<BusStop> busStops = jsonSupport.fromJson(json, new TypeReference<List<BusStop>>() {});
    assert busStops != null : "Bus stops list is empty";

    busStopRepository.saveAll(busStops);
  }

  @Transactional
  public void loadRoutes(String json) throws IOException {
    List<RouteTimetable> routes = jsonSupport.fromJson(json, new TypeReference<List<RouteTimetable>>() {});
    assert routes != null : "Routes list is empty";

    routes.forEach(r -> {
      Route route = saveRoute(r.getName());
      saveTimetable(route, r.getBusStops());
    });
  }

  private Route saveRoute(String name) {
    Route route = new Route();
    route.setName(name);
    return routeRepository.save(route);
  }

  private void saveTimetable(Route route, List<BusStopTime> stopTimes) {
    if (stopTimes == null) return;

    stopTimes.forEach(t -> {
      BusStop busStop = busStopRepository.findByName(t.getName());

      Timetable timetable = new Timetable();
      timetable.setRoute(route);
      timetable.setBusStop(busStop);
      timetable.setPickupTime(t.getTime());

      timetableRepository.save(timetable);
    });
  }

}
