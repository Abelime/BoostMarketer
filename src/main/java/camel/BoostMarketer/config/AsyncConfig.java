package camel.BoostMarketer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class AsyncConfig {
    @Bean(name = "customTaskExecutor")
    @Primary
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
}