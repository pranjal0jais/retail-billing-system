package com.pranjal.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager getCacheManager() {
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of(
                buildCache("categories", 0, 500),
                buildCache("products", 0, 500),
                buildCache("lowStocks", 120, 200),
                buildCache("reportSummary", 180, 50),
                buildCache("reportDaily", 180, 50),
                buildCache("reportTopSelling", 180, 50)
        ));

        return manager;
    }

    private CaffeineCache buildCache(String name, long ttl, int maxSize) {
        Caffeine<Object, Object> builder = Caffeine.newBuilder().maximumSize(maxSize);
        if (ttl > 0) {
            builder.expireAfterWrite(ttl, TimeUnit.SECONDS);
        }
        return new CaffeineCache(name, builder.build());
    }
}
