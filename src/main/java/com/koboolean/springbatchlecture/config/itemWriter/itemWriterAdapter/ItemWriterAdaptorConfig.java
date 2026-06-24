package com.koboolean.springbatchlecture.config.itemWriter.itemWriterAdapter;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
//@Configuration
public class ItemWriterAdaptorConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

//    @Bean
    public Job itemWriterAdaptorJob() {
        return new JobBuilder("itemWriterAdaptorJob", jobRepository)
                .start(itemWriterAdaptorStep())
                .build();
    }

//    @Bean
    public Step itemWriterAdaptorStep() {
        return new StepBuilder("itemWriterAdaptorStep", jobRepository)
                .<String, String>chunk(5, transactionManager)
                .reader(new ItemReader<String>() {

                    int i = 0;

                    @Nullable
                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;

                        return i > 10 ? null : "item " + i;
                    }
                })
                .writer(customItemWriter())
                .build();
    }

//    @Bean
    public ItemWriter<String> customItemWriter() {
        ItemWriterAdapter<String> adapter = new ItemWriterAdapter<>();

        adapter.setTargetObject(customService());
        adapter.setTargetMethod("customWrite");

        return adapter;

    }

    private CustomService customService() {
        return new CustomService();
    }

}
