package com.items.config.properties;

import java.time.ZoneId;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties("items")
@Data
public class ItemsApiProperties {

  private String timeZone;
  private String itemsCache;

  public ZoneId getDefaultZoneId() {
    return ZoneId.of(timeZone);
  }

}