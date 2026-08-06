package com.koboolean.springbatchlecture.config.multi.parallel;

import com.koboolean.springbatchlecture.config.multi.multiThread.StopWatchJobListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ParallelStepsConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job parallelStepsJob() throws InterruptedException {
        return new JobBuilder("parallelStepsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(parallelStepsFlow1())
                .split(parallelStepsTaskExecutor())
                .add(parallelStepsFlow2())
                .end()
                .listener(new StopWatchJobListener())
                .build();
    }

    @Bean
    public Flow parallelStepsFlow1() throws InterruptedException {
        return new FlowBuilder<Flow>("parallelStepsFlow1")
                .start(parallelStepsStep1())
                .build();
    }

    @Bean
    public Flow parallelStepsFlow2() throws InterruptedException {
        return new FlowBuilder<Flow>("parallelStepsFlow2")
                .start(parallelStepsStep2())
                .next(parallelStepsStep3())
                .build();
    }

    @Bean
    public Step parallelStepsStep1() {
        return new StepBuilder("parallelStepsStep1", jobRepository)
                .tasklet(tasklet(), transactionManager).build();
    }

    @Bean
    public Step parallelStepsStep2() {
        return new StepBuilder("parallelStepsStep2", jobRepository)
                .tasklet(tasklet(), transactionManager).build();
    }

    @Bean
    public Step parallelStepsStep3() {
        return new StepBuilder("parallelStepsStep3", jobRepository)
                .tasklet(tasklet(), transactionManager).build();
    }

    @Bean
    public Tasklet tasklet() {
        return new ParallelStepsCustomTasklet();
    }

    @Bean
    public TaskExecutor parallelStepsTaskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4); // thread 생성 갯수
        taskExecutor.setMaxPoolSize(8); // thread를 생성한 이후에 값이 더 있을 경우 추가로 생성될 최대 갯수
        taskExecutor.setThreadNamePrefix("async-thread");

        return taskExecutor;
    }
}
