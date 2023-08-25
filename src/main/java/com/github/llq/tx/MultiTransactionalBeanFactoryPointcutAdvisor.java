package com.github.llq.tx;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

import java.lang.reflect.Method;

/**
 * @author luqi.liu
 * @date 2023/8/24 17:23
 **/
public class MultiTransactionalBeanFactoryPointcutAdvisor extends AbstractBeanFactoryPointcutAdvisor {
    private static final long serialVersionUID = 8337035096231104507L;

    private TransactionAttributeSource transactionAttributeSource;

    private final StaticMethodMatcherPointcut pointcut = new StaticMethodMatcherPointcut() {

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            TransactionAttributeSource tas = getTransactionAttributeSource();
            return (tas == null || tas.getTransactionAttribute(method, targetClass) != null);
        }
    };

    public MultiTransactionalBeanFactoryPointcutAdvisor() {

    }

    public TransactionAttributeSource getTransactionAttributeSource() {
        return transactionAttributeSource;
    }

    public MultiTransactionalBeanFactoryPointcutAdvisor setTransactionAttributeSource(TransactionAttributeSource transactionAttributeSource) {
        this.transactionAttributeSource = transactionAttributeSource;
        return this;
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }
}
