package com.droar.ocr.redaction.api.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"classpath:/spring/spring-beans.xml",})
public class AppConfiguration {

  /** This place is for us to put java beans as usual **/

  @Bean
  public String iAmJustADummyBean() {
    return "IAmADummyBean";
  }
}
