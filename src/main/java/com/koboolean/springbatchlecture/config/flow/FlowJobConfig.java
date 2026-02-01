package com.koboolean.springbatchlecture.config.flow;

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

    @Bean
    public Job flowJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("flowJob", jobRepository)
                .start(flowA(jobRepository, transactionManager)) // 처음 시작하는 Step
                .next(step3(jobRepository, transactionManager))
                .next(flowB(jobRepository, transactionManager))
                .next(step6(jobRepository, transactionManager))
                .end()
                .build();
    }

    @Bean
    public Flow flowA(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new FlowBuilder<Flow>("flowA")
                .start(step1(jobRepository, transactionManager))
                .next(step2(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 1 executed!");
                    return RepeatStatus.FINISHED;
                }), transactionManager).build();
    }

    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step2", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 2 executed!");
                    return RepeatStatus.FINISHED;
                }), transactionManager).build();
    }

    @Bean
    public Step step3(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step3", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 3 executed!");
                    return RepeatStatus.FINISHED;
                }), transactionManager).build();
    }


    @Bean
    public Flow flowB(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new FlowBuilder<Flow>("flowB")
                .start(step4(jobRepository, transactionManager))
                .next(step5(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step step4(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step4", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 4 executed!");
                    return RepeatStatus.FINISHED;
                }), transactionManager).build();
    }

    @Bean
    public Step step5(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step5", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 5 executed!");
                    return RepeatStatus.FINISHED;
                }), transactionManager).build();
    }

    @Bean
    public Step step6(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step6", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 6 executed!");
                    return RepeatStatus.FINISHED;
                }), transactionManager).build();
    }
}
