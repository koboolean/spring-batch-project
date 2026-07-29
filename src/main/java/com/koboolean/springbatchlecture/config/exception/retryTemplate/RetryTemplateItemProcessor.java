package com.koboolean.springbatchlecture.config.exception.retryTemplate;

import com.koboolean.springbatchlecture.config.exception.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.classify.Classifier;
import org.springframework.lang.NonNull;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.DefaultRetryState;
import org.springframework.retry.support.RetryTemplate;

@RequiredArgsConstructor
public class RetryTemplateItemProcessor implements ItemProcessor<String, Customer> {

    private final RetryTemplate retryTemplate;
    int cnt = 0;

    @Override
    public Customer process(@NonNull String item) throws Exception {

        // rollback 여부를 지정한다.
        Classifier<Throwable, Boolean> classifier = new BinaryExceptionClassifier(true);

        Customer customer = retryTemplate.execute(new RetryCallback<Customer, RuntimeException>() {

            @Override
            public Customer doWithRetry(RetryContext context) throws RuntimeException {
                // 지정 횟수만큼 재시도
                cnt++;
                if(item.equals("1") || item.equals("2")) throw new RetryableException("Failed Cnt : " + cnt);

                return new Customer(item);
            }

        }, new RecoveryCallback<Customer>() {
            @Override
            public Customer recover(RetryContext context) throws Exception {
                // 재시도 시에도 실패 시 처리 로직
                return new Customer(item);
            }
        },
        // 실패 시 default로 지정할 설정을 정의한다.
        new DefaultRetryState(item, classifier));

        return customer;
    }
}
