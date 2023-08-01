package com.items.service.cache;

import static java.util.Arrays.asList;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Component;

import com.items.config.properties.ItemsApiProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CacheCustomizer implements CacheManagerCustomizer<ConcurrentMapCacheManager> {

  private final ItemsApiProperties itemsApiProperties;

  @Override
  public void customize(ConcurrentMapCacheManager cacheManager) {
    cacheManager.setCacheNames(asList(itemsApiProperties.getItemsCache()));
  }
}