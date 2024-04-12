package camel.BoostMarketer.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class ScheduleConfig {

    private final JobLauncher jobLauncher;

//    private final Job job;
//
//    @Scheduled(cron = "0 0 0 * * ?")
//    public void runBlogDataJob() {
//        try {
//            // JobParameters can be used to pass in dynamic values or simply a unique parameter to ensure job instance is unique
//            JobParameters params = new JobParametersBuilder()
//                    .addLong("time", System.currentTimeMillis())
//                    .toJobParameters();
//
//            jobLauncher.run(job, params);
//        } catch (Exception e) {
//            log.error("Failed to execute apiJob", e);
//        }
//    }

}