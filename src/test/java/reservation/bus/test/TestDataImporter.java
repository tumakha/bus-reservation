package reservation.bus.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import reservation.bus.importer.BusRoutesImporter;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * @author Yuriy Tumakha
 */
@Component
public class TestDataImporter {

  @Value("classpath:import/bus-stops-test.json")
  private Resource busStopsJson;

  @Value("classpath:import/routes-test.json")
  private Resource routesJson;

  @Autowired
  private BusRoutesImporter busRoutesImporter;

  @PostConstruct
  public void loadTestData() throws IOException {
    busRoutesImporter.load(busStopsJson, routesJson);
  }

}
