package com.example.dividend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    ThreadPoolTaskScheduler threadPool = new ThreadPoolTaskScheduler();
    //스케쥴러를 사용 가능한 프로세서 만큼의 스레드에서 동시에 돌릴수 있게한다.
    int n = Runtime.getRuntime().availableProcessors();
    threadPool.setPoolSize(n);
    threadPool.initialize();
    taskRegistrar.setTaskScheduler(threadPool);
  }
}
