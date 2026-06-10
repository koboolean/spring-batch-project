package com.koboolean.springbatchlecture.config.itemReader.xmlStaxEventItemReader;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

//@Component
public class XMLJobRunner implements ApplicationRunner {

    private final JobLauncher jobLauncher;
    private final Job job;

    public XMLJobRunner(JobLauncher jobLauncher, @Qualifier("xmlBatchJob") Job job){
            this.jobLauncher = jobLauncher;
            this.job = job;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // parameters를 정의한다.
        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("local_date_time", LocalDateTime.now())
                .toJobParameters();

        // Job을 실행한다.
        jobLauncher.run(job,jobParameters);
    }

}
