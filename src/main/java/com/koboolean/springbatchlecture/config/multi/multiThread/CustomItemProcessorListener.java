package com.koboolean.springbatchlecture.config.multi.multiThread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.lang.Nullable;

@Slf4j
public class CustomItemProcessorListener implements ItemProcessListener<Customer, Customer> {

    @Override
    public void beforeProcess(Customer item) {

    }

    @Override
    public void afterProcess(Customer item, @Nullable Customer result) {
        log.info("Thread : {}, processing item : {}", Thread.currentThread().getName(), item.id());
    }

    @Override
    public void onProcessError(Customer item, Exception e) {

    }
}
