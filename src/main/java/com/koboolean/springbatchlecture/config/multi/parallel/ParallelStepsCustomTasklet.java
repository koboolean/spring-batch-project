package com.koboolean.springbatchlecture.config.multi.parallel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.lang.Nullable;

@Slf4j
public class ParallelStepsCustomTasklet implements Tasklet {

    private long sum;
    private static final Object LOCK = new Object();

    @Nullable
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        synchronized (LOCK){
            for(int i = 0; i < 1000000000; i++){
                sum++;
            }

            log.info("{} has been executed on thread {}", chunkContext.getStepContext().getStepName(), Thread.currentThread().getName());
            log.info("sum : {}", sum);
        }

        return RepeatStatus.FINISHED;
    }
}
