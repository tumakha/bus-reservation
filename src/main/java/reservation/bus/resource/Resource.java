package reservation.bus.resource;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import reservation.bus.dto.PageWrapper;

import static org.springframework.http.ResponseEntity.ok;

/**
 * @author Yuriy Tumakha
 */
public interface Resource {

  default <T> ResponseEntity<PageWrapper<T>> page(Page<T> page) {
    return ok(new PageWrapper<>(page));
  }

  default ResponseStatusException errorResponse(HttpStatus status, String reason) {
    return new ResponseStatusException(status, reason);
  }

}
