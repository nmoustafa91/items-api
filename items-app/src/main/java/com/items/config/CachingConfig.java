package com.items.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.items.config.properties.ItemsApiProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CachingConfig {

  private  final ItemsApiProperties itemsApiProperties;

  @Bean
  public CacheManager cacheManager() {
    return new ConcurrentMapCacheManager(itemsApiProperties.getItemsCache());
  }
}