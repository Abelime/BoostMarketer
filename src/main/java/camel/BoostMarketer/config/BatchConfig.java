package camel.BoostMarketer.config;

import camel.BoostMarketer.blog.service.BlogBatchService;
import camel.BoostMarketer.config.tasklet.BlogTasklet;
import camel.BoostMarketer.config.tasklet.KeywordTasklet;
import camel.BoostMarketer.keyword.service.KeywordBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final BlogBatchService blogBatchService;

    private final KeywordBatchService keywordBatchService;

    @Bean
    public Job blogDataJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new JobBuilder("blogDataJob", jobRepository)
                .start(blogStep(jobRepository, platformTransactionManager))
                .next(keywordStep(jobRepository, platformTransactionManager))
                .build();
    }

    @Bean
    public Step blogStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("blogStep", jobRepository)
                .tasklet(new BlogTasklet(blogBatchService), platformTransactionManager)
                .build();
    }

    @Bean
    public Step keywordStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("keywordStep", jobRepository)
                .tasklet(new KeywordTasklet(keywordBatchService), platformTransactionManager)
                .build();
    }


}