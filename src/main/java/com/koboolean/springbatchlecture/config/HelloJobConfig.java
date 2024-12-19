package com.koboolean.springbatchlecture.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class HelloJobConfig {

    @Bean
    public JobLauncherApplicationRunner jobLauncherApplicationRunner(JobLauncher jobLauncher, JobExplorer jobExplorer, JobRepository jobRepository) {
        JobLauncherApplicationRunner runner = new JobLauncherApplicationRunner(jobLauncher, jobExplorer, jobRepository);
        runner.setJobName("helloJob");
        return runner;
    }

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
                    System.out.println(" ============================");
                    System.out.println(" >> Hello Spring Batch");
                    System.out.println(" ============================");
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }

    @Bean
    public Step helloStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("helloStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(" ============================");
                    System.out.println(" >> Step2 has executed");
                    System.out.println(" ============================");
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }

}

