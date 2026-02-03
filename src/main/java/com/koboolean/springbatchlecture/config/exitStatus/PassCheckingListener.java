package com.koboolean.springbatchlecture.config.exitStatus;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.lang.Nullable;

public class PassCheckingListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        StepExecutionListener.super.beforeStep(stepExecution);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        String exitCode = stepExecution.getExitStatus().getExitCode();

        if(!exitCode.equals(ExitStatus.FAILED.getExitCode())){
            // 종료코드가 FAILED가 아니면 새로운 ExitStatus 코드를 생성한다.
            return new ExitStatus("PASS", "Description");
        }

        return null;
    }
}
