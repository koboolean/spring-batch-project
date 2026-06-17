package com.koboolean.springbatchlecture.config.itemReader.jdbcCursorItemReader;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@RequiredArgsConstructor
// @Configuration
public class JdbcCursorItemReaderConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

//    @Bean
    public Job jdbcBatchJob() {
        return new JobBuilder("jdbcBatchJob", jobRepository)
                .start(jdbcBatchStep1())
                .build();
    }

//    @Bean
    public Step jdbcBatchStep1() {
        return new StepBuilder("jdbcBatchStep1", jobRepository)
                .<Customer, Customer>chunk(100, transactionManager)
                .reader(customerItemReader())
                .writer(customerItemWriter())
                .build();
    }

//    @Bean
    public ItemReader<Customer> customerItemReader() {
        int chunkSize = 10;

        return new JdbcCursorItemReaderBuilder<Customer>()
                .name("jdbcCursorItemReader")
                .fetchSize(chunkSize)
                .sql("""
                    select id,
                           "firstName",
                           "lastName",
                           birthdate
                    from customer
                    where "firstName" like ?
                    order by "lastName", "firstName"
                    """)
                .beanRowMapper(Customer.class)
                .queryArguments("%")
                .dataSource(dataSource)
                .build();
    }

    private ItemWriter<Customer> customerItemWriter() {
        return items -> {
            for(Customer c : items){
                System.out.println("Data : " + c.toString());
            }
        };
    }

}
