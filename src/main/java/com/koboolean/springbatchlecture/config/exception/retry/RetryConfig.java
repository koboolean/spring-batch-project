package com.koboolean.springbatchlecture.config.exception.retry;

import com.koboolean.springbatchlecture.config.exception.RetryableException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RetryConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job retryJob() {
        return new JobBuilder("retryJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(retryStep())
                .build();
    }

    @Bean
    public Step retryStep() {
        return new StepBuilder("retryStep", jobRepository)
                .<String, String>chunk(5, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(items -> items.forEach(log::info))
                .faultTolerant()
                .skip(RetryableException.class)
                .skipLimit(2)
                .retryPolicy(retryPolicy())
                .build();
    }

    @Builder
    public RetryPolicy retryPolicy() {
        Map<Class<? extends Throwable>, Boolean> ec = new HashMap<>();
        ec.put(RetryableException.class, true);

        return new SimpleRetryPolicy(2, ec);
    }

    @Builder
    public ItemProcessor<? super String, String> processor() {

        return new RetryItemProcessor();
    }

    @Builder
    public ItemReader<String> reader() {
        List<String> items = new ArrayList<>();

        for(int i = 0; i < 30; i++){
            items.add(String.valueOf(i));
        }

        return new ListItemReader<>(items);
    }
}
