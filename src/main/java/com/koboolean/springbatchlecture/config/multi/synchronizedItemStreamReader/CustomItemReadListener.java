package com.koboolean.springbatchlecture.config.multi.synchronizedItemStreamReader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;

@Slf4j
public class CustomItemReadListener implements ItemReadListener<Customer> {

    @Override
    public void afterRead(Customer item) {
        log.info("Thread : {}, ID : {}", Thread.currentThread().getName(), item.id());
    }
}
