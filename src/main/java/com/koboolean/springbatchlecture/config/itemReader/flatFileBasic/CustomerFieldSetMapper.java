package com.koboolean.springbatchlecture.config.itemReader.flatFileBasic;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class CustomerFieldSetMapper implements FieldSetMapper<Customer> {

    @Override
    public Customer mapFieldSet(FieldSet fs) throws BindException {

        return Customer.builder()
                .name(fs.readString(0))
                .age(fs.readInt(1))
                .year(fs.readString(2))
                .build();
    }

}
