package com.koboolean.springbatchlecture.config.itemProcessor.classifierCompositeItemProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.ClassifierCompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
//@Configuration
@Slf4j
public class ClassifierConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

//    @Bean
    public Job classifierJob() {
        return new JobBuilder("classifierJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(classifierStep())
                .build();
    }

//    @Bean
    public Step classifierStep() {
        return new StepBuilder("classifierStep", jobRepository)
                .<ProcessInfo, ProcessInfo>chunk(10, transactionManager)
                .reader(new ItemReader<ProcessInfo>() {

                    int i = 0;

                    @Nullable
                    @Override
                    public ProcessInfo read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

                        i++;
                        ProcessInfo processInfo = ProcessInfo.builder().id(i).build();

                        return i > 3 ? null : processInfo;
                    }
                })
                .processor(customItemProcessor())
                .writer((chunk) -> {
                    log.info("Data : {}", chunk);
                })
                .build();
    }

//    @Bean
    public ItemProcessor<ProcessInfo,ProcessInfo> customItemProcessor() {
        ClassifierCompositeItemProcessor<ProcessInfo, ProcessInfo> processor = new ClassifierCompositeItemProcessor<>();

        ProcessorClassifier<ProcessInfo, ItemProcessor<?, ? extends ProcessInfo>> classifier = new ProcessorClassifier<>();

        Map<Integer, ItemProcessor<ProcessInfo, ProcessInfo>> processorMap = new HashMap<>();
        processorMap.put(1, new CustomItemProcessor());
        processorMap.put(2, new CustomItemProcessor2());
        processorMap.put(3, new CustomItemProcessor3());

        classifier.setProcessorMap(processorMap);
        processor.setClassifier(classifier);

        return processor;
    }
}
