package com.github.alexeyhved.manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
public class BlockingPoolConfiguration {
    @Bean(destroyMethod = "dispose")
    public Scheduler blockingPool() {
          return Schedulers.newBoundedElastic(
                  10,
                  100,
                  "blockingPool",
                  20,
                  false
          );
    }
}
