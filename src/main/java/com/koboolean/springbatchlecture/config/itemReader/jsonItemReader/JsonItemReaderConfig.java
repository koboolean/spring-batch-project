package com.koboolean.springbatchlecture.config.itemReader.jsonItemReader;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
//@Configuration
public class JsonItemReaderConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

//    @Bean
    public Job xmlBatchJob() {
        return new JobBuilder("jsonBatchJob", jobRepository)
                .start(step1())
                .build();
    }

//    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .<Customer, Customer>chunk(5, transactionManager)
                .reader(customerItemReader())
                .writer(customerItemWriter())
                .build();
    }

    private ItemWriter<Customer> customerItemWriter() {
        return items -> {
            for(Customer c : items){
                System.out.println("Data : " + c.toString());
            }
        };
    }

//    @Bean
    public ItemReader<? extends Customer> customerItemReader() {
        return new JsonItemReaderBuilder<Customer>()
                .name("jsonReader")
                .resource(new ClassPathResource("customer.json"))
                .jsonObjectReader(new JacksonJsonObjectReader<>(Customer.class))
                .build();
    }

}
