package com.koboolean.springbatchlecture.config.itemWriter.databaseItemWriter.jpaItemWriter;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class CustomItemProcessor implements ItemProcessor<Customer, Customer2> {

    @Nullable
    @Override
    public Customer2 process(@NonNull Customer item) throws Exception {
        return Customer2.builder()
                .firstName(item.getFirstName())
                .lastName(item.getLastName())
                .birthdate(item.getBirthdate())
                .build();
    }

}
