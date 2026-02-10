package com.koboolean.springbatchlecture.config.decider;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.lang.Nullable;

public class OddDecider implements JobExecutionDecider {

    private int count = 1;

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, @Nullable StepExecution stepExecution) {

        count++;

        return switch(count % 2){
            case 0 -> new FlowExecutionStatus("EVEN");
            case 1 -> new FlowExecutionStatus("ODD");
            default -> throw new IllegalStateException("Invalid count");
        };
    }
}
