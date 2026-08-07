package com.koboolean.springbatchlecture.config.multi.partitioning;

import com.koboolean.springbatchlecture.config.multi.multiThread.Customer;
import com.koboolean.springbatchlecture.config.multi.multiThread.CustomerRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class PartitioningConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;


    @Bean
    public Job partitioningJob(){
        return new JobBuilder("partitioningJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(partitioningMasterStep())
                .build();
    }

    @Bean
    public Step partitioningMasterStep() {
        return new StepBuilder("partitioningMasterStep", jobRepository)
                .partitioner(partitioningSlaveStep().getName(), partitioningPartitioner())
                .step(partitioningSlaveStep())
                .gridSize(4)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public Partitioner partitioningPartitioner() {
        ColumnRangePartitioner partitioner = new ColumnRangePartitioner();

        partitioner.setColumn("id");
        partitioner.setDataSource(dataSource);
        partitioner.setTable("customer");

        return partitioner;
    }

    @Bean
    public Step partitioningSlaveStep() {
        return new StepBuilder("partitioningSlaveStep", jobRepository)
                .<Customer, Customer>chunk(100, transactionManager)
                .reader(partitioningItemReader(null, null))
                .writer(partitioningItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<Customer> partitioningItemReader(@Value("#{stepExecutionContext['minValue']}") Long minValue, @Value("#{stepExecutionContext['maxValue']}") Long maxValue) {

        log.info("Reading MinValue : {}, MaxValue : {}", minValue, maxValue);

        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(this.dataSource);
        reader.setPageSize(1000);
        reader.setRowMapper(new CustomerRowMapper());

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id, first_name, last_name, birthdate");
        queryProvider.setFromClause("from customer");
        queryProvider.setWhereClause("where id >= " + minValue + " and id <= " + maxValue);
        Map<String, Order> sortKeys = new HashMap<>(1);

        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        reader.setQueryProvider(queryProvider);

        return reader;
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Customer> partitioningItemWriter() {
        JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();

        itemWriter.setDataSource(this.dataSource);
        itemWriter.setSql("insert into customer2 values (:id, :firstName, :lastName, :birthdate)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }
}
