package com.koboolean.springbatchlecture.config.flatFileBasic;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;


@RequiredArgsConstructor
@Configuration
public class FlatFilesWithFixedLengthTokenize {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job flatFilesJob() {
        return new JobBuilder("flatFilesJob", jobRepository)
                .start(step1())
                .next(step2())
                .build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .chunk(5, transactionManager)
                .reader(itemReader())
                .writer(items -> {
                    System.out.println("items = " + items);
                })
                .build();
    }

    @Bean
    public Step step2() {
        return new StepBuilder("step2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step2 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    public FlatFileItemReader itemReader() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("flatFile")
                .resource(new ClassPathResource("customer.txt"))
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
                .targetType(Customer.class)
                .linesToSkip(1)
                .fixedLength()
                .strict(false)
                .addColumns(new Range(1,5))
                .addColumns(new Range(6,9))
                .addColumns(new Range(10,11))
                .names("name","year","age")
                .build();
    }
}
