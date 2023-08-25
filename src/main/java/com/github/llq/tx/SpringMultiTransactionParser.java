package com.github.llq.tx;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.TransactionAnnotationParser;
import org.springframework.transaction.interceptor.NoRollbackRuleAttribute;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author luqi.liu
 * @date 2023/8/24 18:04
 **/
public class SpringMultiTransactionParser implements TransactionAnnotationParser, Serializable {
    private static final long serialVersionUID = 6347912453503932771L;

    @Override
    public boolean isCandidateClass(Class<?> targetClass) {
        return AnnotationUtils.isCandidateClass(targetClass, MultiTransactional.class);
    }

    @Override
    public TransactionAttribute parseTransactionAnnotation(AnnotatedElement element) {
        AnnotationAttributes attributes = AnnotatedElementUtils.findMergedAnnotationAttributes(
                element, MultiTransactional.class, false, false);
        if (attributes != null) {
            return parseMultiTransactionAnnotation(attributes);
        }
        else {
            return null;
        }
    }

    private MultiTransactionAttribute parseMultiTransactionAnnotation(AnnotationAttributes attributes) {
        List<RollbackRuleAttribute> rollbackRules = new ArrayList<>();
        for (Class<?> rbRule : attributes.getClassArray("rollbackFor")) {
            rollbackRules.add(new RollbackRuleAttribute(rbRule));
        }
        for (String rbRule : attributes.getStringArray("rollbackForClassName")) {
            rollbackRules.add(new RollbackRuleAttribute(rbRule));
        }
        for (Class<?> rbRule : attributes.getClassArray("noRollbackFor")) {
            rollbackRules.add(new NoRollbackRuleAttribute(rbRule));
        }
        for (String rbRule : attributes.getStringArray("noRollbackForClassName")) {
            rollbackRules.add(new NoRollbackRuleAttribute(rbRule));
        }


        String[] values = attributes.getStringArray("value");
        List<TransactionAttribute> transactionAttributeList = Arrays.stream(values).filter(StringUtils::isNotBlank).distinct().map(value -> {
            RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();
            rbta.setQualifier(value);
            rbta.setPropagationBehavior(Propagation.REQUIRED.value());
            rbta.setIsolationLevel(Isolation.DEFAULT.value());
            rbta.setTimeout(TransactionDefinition.TIMEOUT_DEFAULT);
            rbta.setTimeoutString(StringUtils.EMPTY);
            rbta.setReadOnly(false);
            rbta.setLabels(Collections.emptyList());
            rbta.setRollbackRules(rollbackRules);
            return rbta;
        }).collect(Collectors.toList());

        MultiTransactionAttribute rbta = new MultiTransactionAttribute(transactionAttributeList);
        rbta.setQualifier(StringUtils.join(values, "##"));
        rbta.setPropagationBehavior(Propagation.REQUIRED.value());
        rbta.setIsolationLevel(Isolation.DEFAULT.value());
        rbta.setTimeout(TransactionDefinition.TIMEOUT_DEFAULT);
        rbta.setTimeoutString(StringUtils.EMPTY);
        rbta.setReadOnly(false);
        rbta.setLabels(Collections.emptyList());
        rbta.setRollbackRules(rollbackRules);
        return rbta;
    }
}
