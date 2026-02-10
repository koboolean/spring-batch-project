package com.koboolean.springbatchlecture.config.scope;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Configuration
public class ScopeJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job scopeJob() {
        return new JobBuilder("scopeJob", jobRepository)
                .start(step1(null)) // 런타임에러 방지를 위해 null을 삽입한다.
                .listener(new JobListener())
                .build();
    }

    @Bean
    // @JobScope
    public Step step1(@Value("#{jobParameters['message']}") String message) {

        System.out.printf("message %s", message);

        return new StepBuilder("step1", jobRepository)
                .tasklet(tasklet(null, null), transactionManager)
                .listener(new StepListener())
                .build();
    }

    @Bean
    @StepScope
    public Tasklet tasklet(@Value("#{jobExecutionContext['name']}") String name, @Value("#{stepExecutionContext['name2']}") String name2){

        System.out.printf("name %s %s", name, name2);

        return ((contribution, chunkContext) -> {
            System.out.println("Step 1 executed!");
            return RepeatStatus.FINISHED;
        });
    }
}
