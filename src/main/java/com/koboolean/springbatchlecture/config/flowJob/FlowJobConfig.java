package com.koboolean.springbatchlecture.config.flowJob;

import com.koboolean.springbatchlecture.config.decider.OddDecider;
import com.koboolean.springbatchlecture.config.exitStatus.PassCheckingListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
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
// @Configuration
public class FlowJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job flowJob() {
        return new JobBuilder("flowJob", jobRepository)
                .start(step())
                .next(decider())
                .from(decider()).on("EVEN").to(evenStep())
                .from(decider()).on("ODD").to(oddStep())
                .end()
                .build();
    }

    @Bean
    public Flow flow(){
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow");

        flowBuilder.start(step())
                .on("SUCCESS")
                .to(evenStep())
                .end();

        return flowBuilder.build();
    }


    @Bean
    public JobExecutionDecider decider() {
        return new OddDecider();
    }

    @Bean
    public Step step() {
        return new StepBuilder("step1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 1 executed!");
                    //contribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                }), transactionManager).build();
    }

    @Bean
    public Step evenStep() {
        return new StepBuilder("step2", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 2 executed!");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .listener(new PassCheckingListener())
                .build();
    }

    @Bean
    public Step oddStep() {
        return new StepBuilder("step3", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 3 executed!");
                    return RepeatStatus.FINISHED;
                }), transactionManager).build();
    }
}
