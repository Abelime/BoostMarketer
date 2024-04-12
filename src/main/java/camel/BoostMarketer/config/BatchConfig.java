package camel.BoostMarketer.config;

import camel.BoostMarketer.blog.service.BlogBatchService;
import camel.BoostMarketer.keyword.service.KeywordBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final BlogBatchService blogBatchService;

    private final KeywordBatchService keywordBatchService;

//    @Bean
//    public Job blogDataJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
//        return new JobBuilder("blogDataJob", jobRepository)
//                .start(blogStep(jobRepository, platformTransactionManager))
//                .next(keywordStep(jobRepository, platformTransactionManager))
//                .build();
//    }
//
//    @Bean
//    public Step blogStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
//        return new StepBuilder("blogStep", jobRepository)
//                .tasklet(new BlogTasklet(blogBatchService), platformTransactionManager)
//                .build();
//    }
//
//    @Bean
//    public Step keywordStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
//        return new StepBuilder("keywordStep", jobRepository)
//                .tasklet(new KeywordTasklet(keywordBatchService), platformTransactionManager)
//                .build();
//    }


}