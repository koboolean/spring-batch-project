package com.koboolean.springbatchlecture.config;

import com.koboolean.springbatchlecture.tasklet.CustomTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;
import java.util.concurrent.Flow;

@Configuration
@RequiredArgsConstructor
public class HelloJobConfig {

    @Bean
    public Job helloJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("helloJob", jobRepository)
                .start(helloStep(jobRepository, transactionManager)) // 처음 시작하는 Step
                .next(helloStep2(jobRepository, transactionManager)) // 다음 시작하는 Step
                //.validator(new CustomJobParametersValidator())
                //.validator(new DefaultJobParametersValidator(new String[]{"name","date"},new String[]{"count"}))
                .preventRestart()
                .build();
    }

    @Bean
    public Step helloStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("helloStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {

                    Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();

                    System.out.println(jobParameters);

                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }

    @Bean
    public Step helloStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("helloStep2", jobRepository)
                .tasklet(new CustomTasklet(), transactionManager).build();
    }

}

