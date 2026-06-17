package com.koboolean.springbatchlecture.config.itemReader.jpaPagingItemReader;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class JpaPagingItemReaderConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job jpaBatchJob() {
        return new JobBuilder("jpaBatchJob", jobRepository)
                .start(jpaBatchStep1())
                .build();
    }

    @Bean
    public Step jpaBatchStep1() {
        return new StepBuilder("jpaBatchStep1", jobRepository)
                .<com.koboolean.springbatchlecture.config.itemReader.jpaPagingItemReader.Customer, com.koboolean.springbatchlecture.config.itemReader.jpaPagingItemReader.Customer>chunk(5, transactionManager)
                .reader(customerItemReader())
                .writer(customerItemWriter())
                .build();
    }

    @Bean
    public ItemReader<com.koboolean.springbatchlecture.config.itemReader.jpaPagingItemReader.Customer> customerItemReader() {
        Map<String, Object> param = new HashMap<>();
        param.put("firstname", "%");

        return new JpaPagingItemReaderBuilder<Customer>()
                .name("jpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(5)
                .queryString("select c from Customer c where c.firstName like :firstname")
                .parameterValues(param)
                .build();
    }

    private ItemWriter<com.koboolean.springbatchlecture.config.itemReader.jpaPagingItemReader.Customer> customerItemWriter() {
        return items -> {
            for(com.koboolean.springbatchlecture.config.itemReader.jpaPagingItemReader.Customer c : items){
                System.out.println("Data : " + c.toString());
            }
        };
    }
}
