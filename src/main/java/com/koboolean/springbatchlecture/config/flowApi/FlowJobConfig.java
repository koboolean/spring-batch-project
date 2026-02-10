package com.koboolean.springbatchlecture.config.flowApi;

import com.koboolean.springbatchlecture.config.exitStatus.PassCheckingListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
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
                    .on("COMPLETED")
                    .to(flow2())
                .from(flow1())
                    .on("FAILED")
                    .to(flow3())
                .end()
                .build();
    }

    @Bean
    public Flow flow1(){
        FlowBuilder<Flow> builder = new FlowBuilder<>("myFlow01");

        builder.start(step1())
                .next(step2())
                .end();

        return builder.build();
    }

    @Bean
    public Flow flow2(){
        FlowBuilder<Flow> builder = new FlowBuilder<>("myFlow02");

        builder.start(flow3())
                .next(step5())
                .next(step6())
                .end();

        return builder.build();
    }

    @Bean
    public Flow flow3(){
        FlowBuilder<Flow> builder = new FlowBuilder<>("myFlow03");

        builder.start(step3())
                .next(step4())
                .end();

        return builder.build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 1 executed!");
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

    @Bean
    public Step step4() {
        return new StepBuilder("step4", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 4 executed!");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step step5() {
        return new StepBuilder("step5", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 5 executed!");
                    return RepeatStatus.FINISHED;
                }), transactionManager).build();
    }

    @Bean
    public Step step6() {
        return new StepBuilder("step6", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 6 executed!");
                    return RepeatStatus.FINISHED;
                }), transactionManager).build();
    }
}
