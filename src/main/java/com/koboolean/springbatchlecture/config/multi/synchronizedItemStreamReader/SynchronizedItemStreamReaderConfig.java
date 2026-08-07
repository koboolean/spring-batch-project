package com.koboolean.springbatchlecture.config.multi.synchronizedItemStreamReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SynchronizedItemStreamReaderConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;


    @Bean
    public Job synchronizedItemStreamReaderJob(){
        return new JobBuilder("synchronizedItemStreamReaderJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(synchronizedItemStreamReaderStep())
                .build();
    }

    @Bean
    public Step synchronizedItemStreamReaderStep() {
        return new StepBuilder("synchronizedItemStreamReaderStep", jobRepository)
                .<Customer, Customer>chunk(60, transactionManager)
                .reader(synchronizedItemStreamReaderItemReader())
                .listener(new CustomItemReadListener())
                .writer(synchronizedItemStreamReaderItemWriter())
                .taskExecutor(synchronizedItemStreamReaderTaskExecutor())
                .build();
    }


    @Bean
    @StepScope
    public SynchronizedItemStreamReader<Customer> synchronizedItemStreamReaderItemReader() {
        int fetchSize = 60;

        JdbcCursorItemReader<Customer> reader = new JdbcCursorItemReaderBuilder<Customer>()
                .name("NonSafetyReader")
                .fetchSize(fetchSize)
                .sql("""
                    select id,
                           first_name,
                           last_name,
                           birthdate
                    from customer
                    """)
                .rowMapper(new DataClassRowMapper<>(Customer.class))
                .dataSource(dataSource)
                .build();

        return new SynchronizedItemStreamReaderBuilder<Customer>()
                .delegate(reader)
                .build();
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Customer> synchronizedItemStreamReaderItemWriter() {
        JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();

        itemWriter.setDataSource(this.dataSource);
        itemWriter.setSql("insert into customer2 values (:id, :firstName, :lastName, :birthdate)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }

    @Bean
    public TaskExecutor synchronizedItemStreamReaderTaskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4); // thread 생성 갯수
        taskExecutor.setMaxPoolSize(8); // thread를 생성한 이후에 값이 더 있을 경우 추가로 생성될 최대 갯수
        taskExecutor.setThreadNamePrefix("safety-thread-");

        return taskExecutor;
    }
}
