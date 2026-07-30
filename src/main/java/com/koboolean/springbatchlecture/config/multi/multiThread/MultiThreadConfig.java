package com.koboolean.springbatchlecture.config.multi.multiThread;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MultiThreadConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean
    public Job multiThreadJob() throws InterruptedException {
        return new JobBuilder("multiThreadJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(multiThreadStep())
                .listener(new StopWatchJobListener())
                .build();
    }

    @Bean
    public Step multiThreadStep() throws InterruptedException {
        return new StepBuilder("multiThreadStep", jobRepository)
                .<Customer, Customer>chunk(100, transactionManager)
                .reader(multiThreadItemReader())
                .listener(new CustomItemReadListener())
                .processor(multiThreadItemProcessor())
                .listener(new CustomItemProcessorListener())
                .writer(multiThreadItemWriter())
                .listener(new CustomItemWriterListener())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4); // thread 생성 갯수
        taskExecutor.setMaxPoolSize(8); // thread를 생성한 이후에 값이 더 있을 경우 추가로 생성될 최대 갯수
        taskExecutor.setThreadNamePrefix("async-thread");

        return taskExecutor;
    }

    @Bean
    public ItemProcessor<Customer, Customer> multiThreadItemProcessor() throws InterruptedException {

        // 각각의 문자열을 대문자로 변환해준다.
        return new ItemProcessor<>() {
            @Override
            public Customer process(@NonNull Customer item) throws Exception {
                return new Customer(item.id(), item.firstName().toUpperCase(), item.lastName().toUpperCase(), item.birthdate());
            }
        };
    }

    @Bean
    public JdbcBatchItemWriter<Customer> multiThreadItemWriter() {
        JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();

        itemWriter.setDataSource(this.dataSource);
        itemWriter.setSql("insert into customer2 values (:id, :firstName, :lastName, :birthdate)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }

    @Bean
    public JdbcPagingItemReader<Customer> multiThreadItemReader() {
        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(this.dataSource);
        reader.setPageSize(100);
        reader.setRowMapper(new CustomerRowMapper());

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id, first_name, last_name, birthdate");
        queryProvider.setFromClause("from customer");

        Map<String, Order> sortKeys = new HashMap<>(1);

        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        reader.setQueryProvider(queryProvider);

        return reader;
    }
}
