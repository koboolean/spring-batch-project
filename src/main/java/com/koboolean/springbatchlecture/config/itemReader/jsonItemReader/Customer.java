package com.koboolean.springbatchlecture.config.itemReader.jsonItemReader;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "customer")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Customer {

    private Long id;
    private String name;
    private Integer age;
}
