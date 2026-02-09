package com.koboolean.springbatchlecture.config.simpleFlow;

import com.koboolean.springbatchlecture.config.decider.OddDecider;
import com.koboolean.springbatchlecture.config.exitStatus.PassCheckingListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Configuration
public class FlowJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job flowJob() {
        return new JobBuilder("flowJob", jobRepository)
                .start(flow1())
                .next(step3())
                .end()
                .build();
    }

    @Bean
    public Flow flow1(){
        FlowBuilder<Flow> builder = new FlowBuilder<>("myFlow");

        builder.start(step1())
                .next(step2())
                .end();

        return builder.build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 1 executed!");
                    //contribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                }), transactionManager).build();
    }

    @Bean
    public Step step2() {
        return new StepBuilder("step2", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 2 executed!");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .listener(new PassCheckingListener())
                .build();
    }

    @Bean
    public Step step3() {
        return new StepBuilder("step3", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 3 executed!");
                    return RepeatStatus.FINISHED;
                }), transactionManager).build();
    }
}
