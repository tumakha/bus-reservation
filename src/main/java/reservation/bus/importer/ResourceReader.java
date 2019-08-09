package reservation.bus.importer;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.util.StreamUtils.copyToString;

/**
 * @author Yuriy Tumakha
 */
public interface ResourceReader {

  default String getResourceAsString(Resource resource) throws IOException {
    return copyToString(resource.getInputStream(), UTF_8);
  }

  default Path getResource(String filename) throws IOException {
      URL resource = getClass().getClassLoader().getResource(filename);
      if (resource != null) {
        try {
          return Paths.get(resource.toURI());
        } catch (URISyntaxException e) {
          throw new IOException(e);
        }
      } else {
        throw new IOException("File not found: " + filename);
      }
  }

  default String getContent(String filename) throws IOException {
    return new String(Files.readAllBytes(getResource(filename)), UTF_8);
  }

}
