package com.koboolean.springbatchlecture.config.flatFileBasic;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Customer {
    private String name;
    private int age;
    private String year;
}
