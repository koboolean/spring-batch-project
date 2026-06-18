package com.koboolean.springbatchlecture.config.itemWriter.jsonFileItemWriter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class Customer {

    private Long id;
    private String firstName;
    private String lastName;
    private String birthdate;

}
