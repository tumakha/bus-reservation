## Bus Reservation API

Bus Reservation REST API - test task

#### Prerequisites

Java 8+, Gradle 5 or gradle-wrapper

#### Run/Debug main class

    reservation.bus.BusReservationApp
    
#### Build

    gradle build

#### Run web app

    cd build/libs
    java -jar bus-reservation.jar

#### HTTP Endpoints

http://localhost:8888 - Site map

http://localhost:8888/swagger-ui.html - Swagger REST Documentation

http://localhost:8888/v1/routes - Bus Routes API

http://localhost:8888/v1/reservations - Reservations API

http://localhost:8888/h2 - H2 Console (Specify **jdbc:h2:mem:testdb** as JDBC URL)

http://localhost:8888/actuator/ - Actuator endpoints
