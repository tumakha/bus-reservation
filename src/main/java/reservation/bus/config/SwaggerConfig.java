package reservation.bus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.InetAddress;
import java.time.LocalTime;

import static java.lang.String.format;
import static springfox.documentation.builders.PathSelectors.regex;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

/**
 * @author Yuriy Tumakha
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

  @Bean
  public Docket apiV1() {
    return docket("v1", "1.0");
  }

  private Docket docket(String group, String version) {
    return new Docket(SWAGGER_2)
        .groupName(group)
        .apiInfo(apiInfo(version))
        .useDefaultResponseMessages(false)
        .directModelSubstitute(LocalTime.class, java.util.Date.class)
        .select()
        .apis(basePackage("reservation.bus"))
        .paths(regex(format("/%s/.*", group)))
        .build();
  }

  private ApiInfo apiInfo(String version) {
    return new ApiInfoBuilder()
        .title("Bus Reservation API")
        .description(format("Host: %s", getHost()))
        .version(version)
        .build();
  }

  private String getHost() {
    try {
      return InetAddress.getLocalHost().getHostName();
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }

}
