package com.koboolean.springbatchlecture.config.itemProcessor.compositeItemProcessor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class CustomItemProcessor2 implements ItemProcessor<String, String> {

    int cnt = 0;

    @Nullable
    @Override
    public String process(@NonNull String item) throws Exception {

        cnt++;

        return item + cnt;
    }
}
