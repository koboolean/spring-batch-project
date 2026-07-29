package com.koboolean.springbatchlecture.config.exception.retry;

import com.koboolean.springbatchlecture.config.exception.RetryableException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

public class RetryItemProcessor implements ItemProcessor<String, String> {

    int cnt = 0;

    @Override
    public String process(@NonNull String item) throws Exception {


        if(item.equals("2") || item.equals("3")){
            cnt++;
            throw new RetryableException("Fail Count : " + cnt);
        }

        return item;
    }
}
