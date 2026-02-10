package com.koboolean.springbatchlecture.config.flowStep;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;

// @Component
public class FlowJobRunner implements ApplicationRunner {

    private final JobLauncher jobLauncher;
    private final Job job;

    public FlowJobRunner(JobLauncher jobLauncher, @Qualifier("flowJob") Job job){
            this.jobLauncher = jobLauncher;
            this.job = job;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // parameters를 정의한다.
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("name", "user1")
                .addLocalDateTime("local_date_time", LocalDate.now().atStartOfDay())
                .addLocalDate("local_date", LocalDate.now())
                .addLong("seq", 1L)
                .addDate("date", new Date())
                .addDouble("double", 1.0)
                .toJobParameters();

        // Job을 실행한다.
        jobLauncher.run(job,jobParameters);
    }

}
