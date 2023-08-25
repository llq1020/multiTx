package com.github.llq.tx;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.List;

/**
 * @author luqi.liu
 * @date 2023/8/24 20:28
 **/
@Configuration(proxyBeanMethods = false)
public class MultiTransactionConfiguration {

    @Bean("multiTransactionAttributeSource")
    public TransactionAttributeSource multiTransactionAttributeSource() {
        return new AnnotationTransactionAttributeSource(new SpringMultiTransactionParser());
    }

    @Bean("multiTransactionAdvisor")
    public MultiTransactionalBeanFactoryPointcutAdvisor multiTransactionAdvisor(TransactionAttributeSource multiTransactionAttributeSource,
                                                                                TransactionInterceptor multiTransactionInterceptor) {
        MultiTransactionalBeanFactoryPointcutAdvisor advisor = new MultiTransactionalBeanFactoryPointcutAdvisor();
        advisor.setTransactionAttributeSource(multiTransactionAttributeSource);
        advisor.setAdvice(multiTransactionInterceptor);
        advisor.setOrder(Integer.MAX_VALUE);
        return advisor;
    }

    @Bean("multiTransactionInterceptor")
    public TransactionInterceptor multiTransactionInterceptor(TransactionAttributeSource multiTransactionAttributeSource) {
        TransactionInterceptor interceptor = new TransactionInterceptor() {
            private static final long serialVersionUID = 624538427205025628L;

            @Override
            protected TransactionManager determineTransactionManager(TransactionAttribute transactionAttribute) {
                if (!(transactionAttribute instanceof MultiTransactionAttribute)) {
                    throw new UnsupportedOperationException("MultiTransaction only support MultiTransactionAttr");
                }
                MultiTransactionAttribute multiTransactionAttribute = (MultiTransactionAttribute) transactionAttribute;
                List<TransactionAttribute> txAttrList = multiTransactionAttribute.getTxAttrs();
                List<TransactionManager> tmList = Lists.newArrayListWithCapacity(txAttrList.size());
                for (TransactionAttribute txAttr : txAttrList) {
                    tmList.add(super.determineTransactionManager(txAttr));
                }
                return new MultiPlatformTransactionManager(tmList);
            }

            @Override
            protected TransactionInfo createTransactionIfNecessary(PlatformTransactionManager tm, TransactionAttribute txAttr, String joinpointIdentification) {
                MultiTransactionAttribute multiTxAttr = (MultiTransactionAttribute) txAttr;
                multiTxAttr.setName(joinpointIdentification);
                return super.createTransactionIfNecessary(tm, txAttr, joinpointIdentification);
            }
        };
        interceptor.setTransactionAttributeSource(multiTransactionAttributeSource);
        return interceptor;
    }
}
