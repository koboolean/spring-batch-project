package com.koboolean.springbatchlecture.config.itemProcessor.compositeItemProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
//@Configuration
@Slf4j
public class CompositionItemConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

//    @Bean
    public Job compositionItemJob() {
        return new JobBuilder("compositionItemJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(compositionItemStep())
                .build();
    }

//    @Bean
    public Step compositionItemStep() {
        return new StepBuilder("compositionItemStep", jobRepository)
                .<String, String>chunk(10, transactionManager)
                .reader(new ItemReader<String>() {
                    int i = 0;
                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        return i > 10 ? null : "item";
                    }
                })
                .processor(customItemProcessor())
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(@NonNull Chunk<? extends String> chunk) throws Exception {
                        log.info("Data : {}", chunk);
                    }
                })
                .build();
    }

//    @Bean
    public ItemProcessor<String, String> customItemProcessor() {
        List<ItemProcessor<String, String>> itemProcessor = new ArrayList();

        itemProcessor.add(new CustomItemProcessor());
        itemProcessor.add(new CustomItemProcessor2());

        return new CompositeItemProcessorBuilder<String, String>()
                .delegates(itemProcessor)
                .build();
    }
}
