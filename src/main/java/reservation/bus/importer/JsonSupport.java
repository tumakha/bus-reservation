package reservation.bus.importer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Yuriy Tumakha
 */
@Component
public class JsonSupport {

  @Autowired
  private ObjectMapper objectMapper;

  public <T> T fromJson(String json, Class<T> valueClass) throws IOException {
    return objectMapper.readValue(json, valueClass);
  }

  public <T> T fromJson(String json, TypeReference valueTypeRef) throws IOException {
    return objectMapper.readValue(json, valueTypeRef);
  }

  public String toJson(Object value) throws IOException {
    return objectMapper.writeValueAsString(value);
  }

}
