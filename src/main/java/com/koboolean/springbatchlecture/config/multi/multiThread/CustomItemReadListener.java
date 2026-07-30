package com.koboolean.springbatchlecture.config.multi.multiThread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;

@Slf4j
public class CustomItemReadListener implements ItemReadListener<Customer> {

    @Override
    public void beforeRead() {

    }

    @Override
    public void afterRead(Customer item) {
        log.info("해당 Thread는 {}, read item : {}", Thread.currentThread().getName(), item.id());
    }

    @Override
    public void onReadError(Exception ex) {

    }
}
