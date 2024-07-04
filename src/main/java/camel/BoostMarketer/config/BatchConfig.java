package camel.BoostMarketer.config;

import camel.BoostMarketer.config.tasklet.BlogTasklet;
import camel.BoostMarketer.config.tasklet.DeleteCheckBlogPostTasklet;
import camel.BoostMarketer.config.tasklet.KeywordCrawlerTasklet;
import camel.BoostMarketer.config.tasklet.KeywordSearchTasklet;
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

    private final BlogTasklet blogTasklet;

    private final DeleteCheckBlogPostTasklet deleteCheckBlogPostTasklet;

    private final KeywordSearchTasklet keywordSearchTasklet;

    private final KeywordCrawlerTasklet keywordCrawlerTasklet;

    @Bean
    public Job blogDataJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new JobBuilder("blogDataJob", jobRepository)
                .start(blogUpdateStep(jobRepository, platformTransactionManager))
                .next(deleteCheckBlogPostStep(jobRepository, platformTransactionManager))
                .next(keywordSearchUpdateStep(jobRepository, platformTransactionManager))
                .next(keywordCrawlerStep(jobRepository, platformTransactionManager))
                .build();
    }

    @Bean
    public Step blogUpdateStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("blogUpdateStep", jobRepository)
                .tasklet(blogTasklet, platformTransactionManager)
                .build();
    }

    @Bean
    public Step deleteCheckBlogPostStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("deleteCheckBlogPostStep", jobRepository)
                .tasklet(deleteCheckBlogPostTasklet, platformTransactionManager)
                .build();
    }

    @Bean
    public Step keywordSearchUpdateStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("keywordSearchUpdateStep", jobRepository)
                .tasklet(keywordSearchTasklet, platformTransactionManager)
                .build();
    }

    @Bean
    public Step keywordCrawlerStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("keywordCrawlerStep", jobRepository)
                .tasklet(keywordCrawlerTasklet, platformTransactionManager)
                .build();
    }


}