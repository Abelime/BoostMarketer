package camel.BoostMarketer.config.tasklet;

import camel.BoostMarketer.keyword.service.KeywordBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KeywordCrawlerTasklet implements Tasklet {

    private final KeywordBatchService keywordBatchService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        keywordBatchService.updateKeywordRank();
        return RepeatStatus.FINISHED;
    }
}