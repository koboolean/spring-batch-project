package com.koboolean.springbatchlecture.config.itemWriter.flatFileItemWriter;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
//@Configuration
public class FlatFilesDelimitedConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

//    @Bean
    public Job delimitedJob() {
        return new JobBuilder("delimitedJob", jobRepository)
                .start(delimitedStep1())
                .build();
    }

//    @Bean
    public Step delimitedStep1() {
        return new StepBuilder("delimitedStep1", jobRepository)
                .<Customer, Customer>chunk(5, transactionManager)
                .reader(customItemReader())
                .writer(customItemWriter())
                .build();
    }

//    @Bean
    public ItemReader<Customer> customItemReader() {
        List<Customer> customers = Arrays.asList(
                new Customer(1L, "TEST1", 10),
                new Customer(2L, "TEST2", 11),
                new Customer(3L, "TEST3", 12));

        return new ListItemReader<>(customers);
    }

//    @Bean
    public ItemWriter<? super Customer> customItemWriter() {
        return new FlatFileItemWriterBuilder<>()
                .name("flatFileWriter")
                .resource(new FileSystemResource("/Users/johyeonjun/MainProject/backend/spring-batch-project/src/main/resources/custom-writer.txt"))
                .delimited()
                .delimiter("|")
                .names(new String[]{"id", "name", "age"})
                .build();
    }
}
