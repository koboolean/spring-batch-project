package com.koboolean.springbatchlecture.config.itemProcessor.classifierCompositeItemProcessor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Slf4j
public class CustomItemProcessor implements ItemProcessor<ProcessInfo, ProcessInfo> {

    @Nullable
    @Override
    public ProcessInfo process(@NonNull ProcessInfo item) throws Exception {

        log.info("CustomItemProcessor 01");

        return item;
    }
}
