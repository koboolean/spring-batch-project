package com.koboolean.springbatchlecture.config.multi.partitioning;

import lombok.Setter;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class ColumnRangePartitioner implements Partitioner {

    private JdbcOperations jdbcTemplate;

    @Setter
    private String table;

    @Setter
    private String column;

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @NonNull
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        int min = jdbcTemplate.queryForObject("SELECT MIN(" + column + ") from " + table, Integer.class);
        int max = jdbcTemplate.queryForObject("SELECT MAX(" + column + ") from " + table, Integer.class);
        int targetSize = (max - min) / gridSize + 1;

        Map<String, ExecutionContext> result = new HashMap<String, ExecutionContext>();
        int number = 0;
        int start = min;
        int end = start + targetSize - 1;

        while (start <= max) {
            ExecutionContext value = new ExecutionContext();
            result.put("partition" + number, value);

            if (end >= max) {
                end = max;
            }
            value.putInt("minValue", start);
            value.putInt("maxValue", end);
            start += targetSize;
            end += targetSize;
            number++;
        }

        return result;
    }}
