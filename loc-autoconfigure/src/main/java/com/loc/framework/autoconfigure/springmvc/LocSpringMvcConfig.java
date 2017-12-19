package com.loc.framework.autoconfigure.springmvc;

import java.util.Optional;
import javax.servlet.Filter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * Created on 2017/11/30.
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties({LocSpringMvcLogProperties.class, LocSpringMvcCorsProperties.class})
public class LocSpringMvcConfig {

  @Bean
  @ConditionalOnClass(CorsFilter.class)
  @ConditionalOnProperty(value = "loc.web.springmvc.cors.enabled")
  public Filter corsFilter(LocSpringMvcCorsProperties locSpringMvcCorsProperties) {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration(
        Optional.ofNullable(locSpringMvcCorsProperties.getPath()).orElse("/**"),
        buildConfig(locSpringMvcCorsProperties));
    return new CorsFilter(source);
  }

  private CorsConfiguration buildConfig(LocSpringMvcCorsProperties locSpringMvcCorsProperties) {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    Optional.ofNullable(locSpringMvcCorsProperties.getAllowOrigins())
        .ifPresent(origins -> origins.forEach(corsConfiguration::addAllowedOrigin));
    Optional.ofNullable(locSpringMvcCorsProperties.getAllowHeaders())
        .ifPresent(headers -> headers.forEach(corsConfiguration::addAllowedHeader));
    Optional.ofNullable(locSpringMvcCorsProperties.getAllowOrigins())
        .ifPresent(methods -> methods.forEach(corsConfiguration::addAllowedMethod));
    Optional.ofNullable(locSpringMvcCorsProperties.getAllowExposeHeaders())
        .ifPresent(headers -> headers.forEach(corsConfiguration::addExposedHeader));
    return corsConfiguration;
  }

  @Bean
  @ConditionalOnClass(OncePerRequestFilter.class)
  @ConditionalOnProperty(value = "loc.web.springmvc.log.enabled", matchIfMissing = true)
  public Filter accessLogFilter(LocSpringMvcLogProperties hnSpringMvcProperties) {
    return new LocAccessLogFilter(hnSpringMvcProperties);
  }
}
