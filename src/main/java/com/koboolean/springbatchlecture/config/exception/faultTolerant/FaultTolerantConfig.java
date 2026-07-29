package com.koboolean.springbatchlecture.config.exception.faultTolerant;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FaultTolerantConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job faultTolerantJob() {
        return new JobBuilder("faultTolerantJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(faultTolerantStep())
                .build();
    }

    @Bean
    public Step faultTolerantStep() {
        return new StepBuilder("faultTolerantStep", jobRepository)
                .<String, String>chunk(5, transactionManager)
                .reader(new ItemReader<String>(){
                    int i = 0;

                    @Nullable
                    @Override
                    public String read() throws Exception {
                        i++;

                        if(i == 1){
                            throw new IllegalAccessException("This Exception is Skipped");
                        }

                        return i > 3 ? null : "item " + i;
                    }
                })
                .processor(new ItemProcessor<String, String>() {
                    @Nullable
                    @Override
                    public String process(@NonNull String item) throws Exception {
                        throw new IllegalStateException("This Exception is Retry");
//                        return item;
                    }
                })
                .writer(chunk -> log.info("Data : {}", chunk))
                .faultTolerant()
                .skip(IllegalAccessException.class)
                .skipLimit(2)
                .retry(IllegalStateException.class)
                .retryLimit(2)
                .build();
    }
}
