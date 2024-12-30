package com.koboolean.springbatchlecture.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class HelloJobRunner implements ApplicationRunner {

    private final JobLauncher jobLauncher;

    private final Job job;

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
