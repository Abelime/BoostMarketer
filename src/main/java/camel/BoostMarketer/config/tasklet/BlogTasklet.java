package camel.BoostMarketer.config.tasklet;

import camel.BoostMarketer.blog.service.BlogBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BlogTasklet implements Tasklet {

    private final BlogBatchService blogBatchService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        blogBatchService.updateBlogPost();
        return RepeatStatus.FINISHED;
    }
}