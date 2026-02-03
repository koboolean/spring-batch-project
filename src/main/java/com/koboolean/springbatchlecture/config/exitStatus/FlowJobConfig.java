package com.koboolean.springbatchlecture.config.exitStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
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

    /**
     * 1. Step1이 실행이 실패한다면 Step2로 가며, Step2가 PASS일 경우 STOP되도록 해보자
     * 2. Step1이 실행된 이후 PASS가 오지 않는다면 JOB은 실패하게 된다.
     * 3.
     * @return
     */
    @Bean
    public Job flowJob() {
        return new JobBuilder("flowJob", jobRepository)
                .start(step1())
                    .on("FAILED")
                    .to(step2())
                    .on("PASS") // 사용자 정의 코드로 반환되었다면
                    .stop()
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 1 executed!");
                    contribution.setExitStatus(ExitStatus.FAILED);
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
