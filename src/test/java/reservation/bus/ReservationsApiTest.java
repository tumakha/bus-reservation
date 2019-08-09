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
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.*;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class ReservationsApiTest implements ResourceReader {

  @LocalServerPort
  private int webPort;

	@Value("classpath:create-reservation-response.json")
	private Resource createReservationResponse;

  @Value("classpath:get-reservations-response.json")
  private Resource getReservationsResponse;

  @Value("classpath:get-reservation-by-id-response.json")
  private Resource getReservationByIdResponse;

	private static final int year = LocalDate.now().getYear() + 1;
	private static final String date = year + "-08-13";

	@Before
  public void setUp() {
    RestAssured.port = webPort;
  }

	@Test
	public void testCreateReservation() throws IOException {
		String response = given().
				contentType(JSON).
				body("{\"busStop\": \"stop2\", \"childName\": \"Olivia\"}").
				post("/v1/reservations/route1/" + date).
				then().
				contentType(JSON).
				statusCode(HTTP_CREATED).
				extract().body().asString();

		assertThatJson(response).isEqualTo(getResourceAsString(createReservationResponse));
	}

	@Test
	public void testCreateReservationUnknownRoute() {
		given().
				contentType(JSON).
				body("{\"busStop\": \"stop2\", \"childName\": \"Oliver\"}").
				post("/v1/reservations/666/" + date).
				then().
				contentType(JSON).
				statusCode(HTTP_NOT_FOUND).
				body("message", equalTo("Route not found by name '666'"),
						"error", equalTo("Not Found"));
	}

	@Test
	public void testCreateReservationWrongDateFormat() {
		given().
				contentType(JSON).
				body("{\"busStop\": \"stop2\", \"childName\": \"Leo\"}").
				post("/v1/reservations/route1/2019-08").
				then().
				contentType(JSON).
				statusCode(HTTP_BAD_REQUEST).
				body("message", containsString("Parse attempt failed for value [2019-08]"),
						"error", equalTo("Bad Request"));
	}

	@Test
	public void testCreateReservationDateIsInThePast() {
		given().
				contentType(JSON).
				body("{\"busStop\": \"stop2\", \"childName\": \"Leo\"}").
				post("/v1/reservations/route1/2019-08-01").
				then().
				contentType(JSON).
				statusCode(HTTP_BAD_REQUEST).
				body("message", equalTo("2019-08-01 is in the past"),
						"error", equalTo("Bad Request"));
	}

	@Test
	public void testCreateReservationDateIsNotSchoolDay() {
		given().
				contentType(JSON).
				body("{\"busStop\": \"stop2\", \"childName\": \"Leo\"}").
				post("/v1/reservations/route1/2022-08-13").
				then().
				contentType(JSON).
				statusCode(HTTP_BAD_REQUEST).
				body("message", equalTo("2022-08-13 is not a school day"),
						"error", equalTo("Bad Request"));
	}

	@Test
	public void testCreateReservationUnknownBusStop() {
		given().
				contentType(JSON).
				body("{\"busStop\": \"UNKNOWN_STOP\", \"childName\": \"Leo\"}").
				post("/v1/reservations/route1/" + date).
				then().
				contentType(JSON).
				statusCode(HTTP_NOT_FOUND).
				body("message", equalTo("Unknown bus stop 'UNKNOWN_STOP' on route 'route1'"),
						"error", equalTo("Not Found"));
	}

	@Test
	public void testCreateReservationChildNameIsEmpty() {
		given().
				contentType(JSON).
				body("{\"busStop\": \"stop2\"}").
				post("/v1/reservations/route1/" + date).
				then().
				contentType(JSON).
				statusCode(HTTP_BAD_REQUEST).
				body("errors[0].field", equalTo("childName"),
						"errors[0].defaultMessage", equalTo("must not be null"),
						"message", containsString("Validation failed for object"),
						"error", equalTo("Bad Request"));
	}

	@Test
	public void testCreateReservationChildNameAlreadyRegistered() {
		// Created
		given().
				contentType(JSON).
				body("{\"busStop\": \"stop3\", \"childName\": \"Emily\"}").
				post("/v1/reservations/route1/" + date).
				then().
				contentType(JSON).
				statusCode(HTTP_CREATED).
				body("busStop", equalTo("stop3"),
						"childName", equalTo("Emily"));

		// Conflict
		given().
				contentType(JSON).
				body("{\"busStop\": \"stop3\", \"childName\": \"Emily\"}").
				post("/v1/reservations/route1/" + date).
				then().
				contentType(JSON).
				statusCode(HTTP_CONFLICT).
				body("message", equalTo("Child already registered"),
						"error", equalTo("Conflict"));
	}

	@Test
	public void testGetReservations() throws IOException {
		// Create reservation for route2
		given().
				contentType(JSON).
				body("{\"busStop\": \"stop4\", \"childName\": \"Amelia\"}").
				post("/v1/reservations/route2/" + date).
				then().
				contentType(JSON).
				statusCode(HTTP_CREATED).
				body("busStop", equalTo("stop4"),
						"childName", equalTo("Amelia"));

		// Get reservations for route2
		String response = when().
				get("/v1/reservations/route2/" + date).
				then().
				contentType(JSON).
				statusCode(HTTP_OK).
				extract().body().asString();

		assertThatJson(response).isEqualTo(getResourceAsString(getReservationsResponse));
	}

  @Test
  public void testUpdateAndGetReservation() throws IOException {
    // Create
    long reservationId = given().
        contentType(JSON).
        body("{\"busStop\": \"stop2\", \"childName\": \"Emily\"}").
        post("/v1/reservations/route1/" + date).
        then().
        contentType(JSON).
        statusCode(HTTP_CREATED).
        body("busStop", equalTo("stop2"),
            "childName", equalTo("Emily")).
        extract().jsonPath().getLong("reservationId");

    // Update
    given().
        contentType(JSON).
        body("{\"busStop\": \"stop3\", \"childName\": \"Emy\"}").
        put("/v1/reservations/route1/" + date + "/" + reservationId).
        then().
        contentType(JSON).
        statusCode(HTTP_OK).
        body("busStop", equalTo("stop3"),
            "childName", equalTo("Emy"));

    // Get
    String response = when().
        get("/v1/reservations/route1/" + date + "/" + reservationId).
        then().
        contentType(JSON).
        statusCode(HTTP_OK).
        extract().body().asString();

    assertThatJson(response).isEqualTo(getResourceAsString(getReservationByIdResponse));
  }

  @Test
  public void testDeleteReservation() {
    // Create
    long reservationId = given().
        contentType(JSON).
        body("{\"busStop\": \"stop2\", \"childName\": \"Lily\"}").
        post("/v1/reservations/route1/" + date).
        then().
        contentType(JSON).
        statusCode(HTTP_CREATED).
        body("childName", equalTo("Lily")).
        extract().jsonPath().getLong("reservationId");

    // Delete
    String response = when().
        delete("/v1/reservations/route1/" + date + "/" + reservationId).
        then().
        contentType(JSON).
        statusCode(HTTP_OK).
        extract().body().asString();

    assertThatJson(response).isEqualTo("{\"status\":\"OK\"}");

    // Get
    when().
        get("/v1/reservations/route1/" + date + "/" + reservationId).
        then().
        contentType(JSON).
        statusCode(HTTP_NOT_FOUND).
        body("message", equalTo("Reservation not found by id = " + reservationId),
            "error", equalTo("Not Found"));
  }


}
