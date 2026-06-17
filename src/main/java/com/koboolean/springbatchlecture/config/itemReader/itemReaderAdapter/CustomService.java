package com.koboolean.springbatchlecture.config.itemReader.itemReaderAdapter;

public class CustomService<T> {

    private int cnt = 0;

    public T customRead(){
        return (T)("item" + cnt++);
    }
}
