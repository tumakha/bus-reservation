package reservation.bus;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import reservation.bus.importer.ResourceReader;

import java.io.IOException;

import static io.restassured.RestAssured.when;
import static io.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class RoutesApiTest implements ResourceReader {

  @LocalServerPort
  private int webPort;

	@Value("classpath:route1.json")
	private Resource route1;

	@Before
  public void setUp() {
    RestAssured.port = webPort;
  }

	@Test
	public void testGetAllRoutes() {
		when().
				get("/v1/routes").
				then().
				statusCode(HTTP_OK).
				contentType(JSON).
				body("content.name", hasItems("route1", "route2"));
	}

	@Test
	public void testGetRouteByName() throws IOException {
		String response = when().
				get("/v1/routes/route1").
				then().
				statusCode(HTTP_OK).
				contentType(JSON).
				extract().body().asString();

		assertThatJson(response).isEqualTo(getResourceAsString(route1));
	}

	@Test
	public void testGetRouteUnknownName() {
		when().
				get("/v1/routes/UnknownName").
				then().
				statusCode(HTTP_NOT_FOUND).
				contentType(JSON).
				body("message", equalTo("Route not found by name 'UnknownName'"),
						"error", equalTo("Not Found"));
	}

}
