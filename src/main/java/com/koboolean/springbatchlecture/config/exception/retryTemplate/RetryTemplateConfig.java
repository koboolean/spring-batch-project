package com.koboolean.springbatchlecture.config.exception.retryTemplate;

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
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RetryTemplateConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job retryTemplateJob() {
        return new JobBuilder("retryTemplateJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(retryTemplateStep())
                .build();
    }

    @Bean
    public Step retryTemplateStep() {
        return new StepBuilder("retryTemplateStep", jobRepository)
                .<String, Customer>chunk(5, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(items -> items.forEach(v -> log.info("Data : {}", v)))
                .build();
    }

    @Bean
    public ItemProcessor<String, Customer> processor() {
        return new RetryTemplateItemProcessor(retryTemplate());
    }

    @Bean
    public ItemReader<String> reader() {
        List<String> items = new ArrayList<>();

        for(int i = 0; i < 30; i++){
            items.add(String.valueOf(i));
        }

        return new ListItemReader<>(items);
    }

    @Bean
    public RetryTemplate retryTemplate(){
        Map<Class<? extends Throwable>, Boolean> ec = new HashMap<>();
        ec.put(RetryableException.class, true);

        SimpleRetryPolicy sp = new SimpleRetryPolicy(2, ec);

        // 재시도 시간 지정
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(2000); // 지연시간 2초

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(sp);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }
}
