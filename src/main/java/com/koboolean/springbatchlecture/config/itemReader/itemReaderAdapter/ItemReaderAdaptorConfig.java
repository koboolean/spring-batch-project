package com.koboolean.springbatchlecture.config.itemReader.itemReaderAdapter;

import com.koboolean.springbatchlecture.config.itemReader.jsonItemReader.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
//@Configuration
public class ItemReaderAdaptorConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

//    @Bean
    public Job adaptorBatchJob() {
        return new JobBuilder("adaptorBatchJob", jobRepository)
                .start(step1())
                .build();
    }

//    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .<String, String>chunk(5, transactionManager)
                .reader(customerItemReader())
                .writer(customerItemWriter())
                .build();
    }

    private ItemWriter<String> customerItemWriter() {
        return items -> {
            for(String c : items){
                System.out.println("Data : " + c);
            }
        };
    }

//    @Bean
    public ItemReader<String> customerItemReader() {

        ItemReaderAdapter<String> reader = new ItemReaderAdapter<>();
        reader.setTargetObject(customService());
        reader.setTargetMethod("customRead");
        return reader;
    }

//    @Bean
    public Object customService() {
        return new CustomService<>();
    }

}
