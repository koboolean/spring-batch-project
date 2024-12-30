package com.koboolean.springbatchlecture.config;

import com.koboolean.springbatchlecture.tasklet.CustomTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class HelloJobConfig {

    @Bean
    public Job helloJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("helloJob", jobRepository)
                .start(helloStep(jobRepository, transactionManager)) // 처음 시작하는 Step
                .next(helloStep2(jobRepository, transactionManager)) // 다음 시작하는 Step
                .build();
    }

    @Bean
    public Step helloStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("helloStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {

                    Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();

                    // Job Execution Context
                    ExecutionContext jobExecutionContext = contribution.getStepExecution().getJobExecution().getExecutionContext();

                    // Step Execution Context
                    ExecutionContext stepExecutionContext = contribution.getStepExecution().getExecutionContext();


                    String jobName = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getJobName();
                    String stepName = chunkContext.getStepContext().getStepExecution().getStepName();

                    if(jobExecutionContext.get("jobName") == null){
                        jobExecutionContext.put("jobName", jobName);
                    }

                    if(stepExecutionContext.get("stepName") == null){
                        stepExecutionContext.put("stepName", stepName);
                    }

                    System.out.println(jobExecutionContext.get("jobName"));
                    System.out.println(stepExecutionContext.get("stepName"));

                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }

    @Bean
    public Step helloStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("helloStep2", jobRepository)
                .tasklet(new CustomTasklet(), transactionManager).build();
    }

}

