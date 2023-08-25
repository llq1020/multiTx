package com.github.llq.tx;

import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

import java.util.List;

/**
 * @author luqi.liu
 * @date 2023/8/24 18:26
 **/
public class MultiTransactionAttribute extends RuleBasedTransactionAttribute {

    private static final long serialVersionUID = 2921757834884908168L;
    private final List<TransactionAttribute> txAttrs;


    public MultiTransactionAttribute(List<TransactionAttribute> txAttrs) {
        this.txAttrs = txAttrs;
    }

    public List<TransactionAttribute> getTxAttrs() {
        return txAttrs;
    }

}
