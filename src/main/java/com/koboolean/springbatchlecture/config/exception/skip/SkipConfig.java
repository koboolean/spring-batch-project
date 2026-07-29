package com.koboolean.springbatchlecture.config.exception.skip;

import com.koboolean.springbatchlecture.config.exception.NoSkippableException;
import com.koboolean.springbatchlecture.config.exception.SkippableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.LimitCheckingItemSkipPolicy;
import org.springframework.batch.core.step.skip.NeverSkipItemSkipPolicy;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SkipConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job skipJob() {
        return new JobBuilder("skipJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(skipStep())
                .build();
    }

    @Bean
    public Step skipStep() {
        return new StepBuilder("skipStep", jobRepository)
                .<String, String>chunk(5, transactionManager)
                .reader(new ItemReader<String>(){
                    int i = 0;

                    @Nullable
                    @Override
                    public String read() throws Exception {
                        i++;

                        if(i == 3){
                            throw new SkippableException("This Exception is Skipped");
                        }
                        log.info("ItemReader : {}", i);
                        return i > 20 ? null : String.valueOf(i);
                    }
                })
                .processor(new ItemProcessor<String, String>() {

                    int cnt = 0;

                    @Override
                    public String process(@NonNull String item) throws Exception {

                        if(item.equals("6") || item.equals("7")){
                            cnt++;
                            throw new SkippableException("Process Failed Count " + cnt);
                        }else{
                            log.info("Item Processor : {}", item);
                        }

                        return String.valueOf(Integer.parseInt(item) * -1);
                    }
                })
                .writer(new ItemWriter<String>() {

                    int cnt = 0;

                    @Override
                    public void write(@NonNull Chunk<? extends String> items) throws Exception {
                        for(String item : items) {
                            if (item.equals("-16")) {
                                cnt++;
                                throw new SkippableException("Write failed cnt : " + cnt);
                            } else {
                                log.info("ItemWriter : {}", item);
                            }
                        }
                    }
                })
                .faultTolerant()
                .noSkip(NoSkippableException.class)
                .skip(SkippableException.class)
                .skipPolicy(new NeverSkipItemSkipPolicy())
                .build();
    }

    @Bean
    public SkipPolicy limitCheckingItemSkipPolicy() {
        Map<Class<? extends Throwable>,Boolean> exceptionClass = new HashMap<>();

        exceptionClass.put(SkippableException.class, true);

        return new LimitCheckingItemSkipPolicy(3, exceptionClass);
    }
}
