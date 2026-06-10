package com.koboolean.springbatchlecture.config.itemReader.flatFileBasic;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
//@Configuration
public class FlatFilesConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    //@Bean
    public Job flatFilesJob() {
        return new JobBuilder("flatFilesJob", jobRepository)
                .start(step1())
                .next(step2())
                .build();
    }

    //@Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .chunk(5, transactionManager)
                .reader(itemReader())
                .writer(items -> {
                    System.out.println("items = " + items);
                })
                .build();
    }

    //@Bean
    public Step step2() {
        return new StepBuilder("step2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step2 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    //@Bean
    public ItemReader itemReader(){
        FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();

        reader.setResource(new ClassPathResource("/customer.csv"));

        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
        lineMapper.setFieldSetMapper(new CustomerFieldSetMapper());

        reader.setLineMapper(lineMapper);
        // 첫번째 라인은 읽지 않는다.
        reader.setLinesToSkip(1);

        return reader;
    }
}
