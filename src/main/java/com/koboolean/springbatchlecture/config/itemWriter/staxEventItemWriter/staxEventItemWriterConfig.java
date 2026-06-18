package com.koboolean.springbatchlecture.config.itemWriter.staxEventItemWriter;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class staxEventItemWriterConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean
    public Job staxEventItemWriterJob() throws Exception {
        return new JobBuilder("staxEventItemWriterJob", jobRepository)
                .start(delimitedStep1())
                .build();
    }

    @Bean
    public Step delimitedStep1() throws Exception {
        return new StepBuilder("delimitedStep1", jobRepository)
                .<Customer, Customer>chunk(5, transactionManager)
                .reader(customerItemReader())
                .writer(customItemWriter())
                .build();
    }

    @Bean
    public ItemReader<Customer> customerItemReader() throws Exception {
        int chunkSize = 5;

        Map<String,Object> param = new HashMap<>();
        param.put("firstname", "%");

        return new JdbcPagingItemReaderBuilder<Customer>()
                .name("jdbcPagingReader")
                .pageSize(chunkSize)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
                .queryProvider(createQueryProvider())
                .parameterValues(param)
                .build();
    }

    @Bean
    public PagingQueryProvider createQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();

        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("id, first_name, last_name, birthdate");
        queryProvider.setFromClause("from customer");
        queryProvider.setWhereClause("where first_name like :firstname");
        Map<String, Order> sortKey = new HashMap<>();
        sortKey.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKey);

        return queryProvider.getObject();
    }

    @Bean
    public ItemWriter<Customer> customItemWriter() {
        return new StaxEventItemWriterBuilder<Customer>()
                .name("staxEventItemWriter")
                .marshaller(itemMarshaller())
                .resource(new FileSystemResource("/Users/johyeonjun/MainProject/backend/spring-batch-project/src/main/resources/custom-writer.xml"))
                .rootTagName("customer")
                .build();
    }

    private Marshaller itemMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Customer.class);

        return marshaller;
    }
}
