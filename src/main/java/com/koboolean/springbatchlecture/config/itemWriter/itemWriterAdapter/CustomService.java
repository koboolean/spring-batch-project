package com.koboolean.springbatchlecture.config.itemWriter.itemWriterAdapter;

public class CustomService<T> {

    public void customWrite(T item){
        System.out.println(item);
    }
}
