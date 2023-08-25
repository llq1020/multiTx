package com.github.llq.tx;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.*;
import org.springframework.transaction.interceptor.TransactionAttribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luqi.liu
 * @date 2023/8/24 14:41
 **/
public class MultiPlatformTransactionManager implements PlatformTransactionManager, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiPlatformTransactionManager.class);

    private static final long serialVersionUID = -6729893091304365270L;
    private final List<TransactionManager> tmList;

    public MultiPlatformTransactionManager(List<TransactionManager> tmList) {
        this.tmList = tmList;
    }

    public List<TransactionManager> getTmList() {
        return tmList;
    }

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
        MultiTransactionAttribute multiTxAttr = (MultiTransactionAttribute) definition;
        List<TransactionAttribute> txAttrList = multiTxAttr.getTxAttrs();
        List<TransactionManager> tmList = getTmList();

        ArrayList<TransactionStatus> transactionStatusList = Lists.newArrayListWithCapacity(txAttrList.size());

        for (int i = 0; i < txAttrList.size(); i++) {
            PlatformTransactionManager subTm = (PlatformTransactionManager) tmList.get(i);
            TransactionAttribute txAttr = txAttrList.get(i);

            transactionStatusList.add(subTm.getTransaction(txAttr));
        }
        return new MultiTransactionStatus(transactionStatusList);
    }

    @Override
    public void commit(TransactionStatus status) throws TransactionException {
        MultiTransactionStatus multiTxStatus = (MultiTransactionStatus) status;
        List<TransactionManager> transactionManagers = getTmList();

        for (int i = transactionManagers.size() - 1; i >= 0; i--) {
            TransactionStatus txStatus = multiTxStatus.getTxStatusList().get(i);
            PlatformTransactionManager tm = (PlatformTransactionManager) transactionManagers.get(i);

            try {
                tm.commit(txStatus);
            } catch (Exception e) {
                LOGGER.error("TM[{}] commit failed!", tm, e);
            }
        }
    }

    @Override
    public void rollback(TransactionStatus status) throws TransactionException {
        MultiTransactionStatus multiTxStatus = (MultiTransactionStatus) status;
        List<TransactionManager> transactionManagers = getTmList();

        for (int i = transactionManagers.size() - 1; i >= 0; i--) {
            TransactionStatus txStatus = multiTxStatus.getTxStatusList().get(i);
            PlatformTransactionManager tm = (PlatformTransactionManager) transactionManagers.get(i);

            try {
                tm.rollback(txStatus);
            } catch (Exception e) {
                LOGGER.error("TM[{}] rollback failed!", tm, e);
            }
        }
    }
}
