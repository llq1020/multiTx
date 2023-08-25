package com.github.llq.tx;

import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionExecution;
import org.springframework.transaction.TransactionStatus;

import java.util.List;

/**
 * @author luqi.liu
 * @date 2023/8/25 10:39
 **/
public class MultiTransactionStatus implements TransactionStatus {

    private final List<TransactionStatus> txStatusList;

    public MultiTransactionStatus(List<TransactionStatus> txStatusList) {
        this.txStatusList = txStatusList;
    }

    public List<TransactionStatus> getTxStatusList() {
        return txStatusList;
    }

    @Override
    public boolean hasSavepoint() {
        throw new UnsupportedOperationException("hasSavepoint not support");
    }

    @Override
    public void flush() {
        getTxStatusList().forEach(TransactionStatus::flush);
    }

    @Override
    public Object createSavepoint() throws TransactionException {
        throw new UnsupportedOperationException("createSavepoint not support");
    }

    @Override
    public void rollbackToSavepoint(Object savepoint) throws TransactionException {
        throw new UnsupportedOperationException("rollbackToSavepoint not support");
    }

    @Override
    public void releaseSavepoint(Object savepoint) throws TransactionException {
        throw new UnsupportedOperationException("releaseSavepoint not support");
    }

    @Override
    public boolean isNewTransaction() {
        return getTxStatusList().stream().anyMatch(TransactionExecution::isNewTransaction);
    }

    @Override
    public void setRollbackOnly() {
        getTxStatusList().forEach(TransactionExecution::setRollbackOnly);
    }

    @Override
    public boolean isRollbackOnly() {
        return getTxStatusList().stream().anyMatch(TransactionExecution::isRollbackOnly);
    }

    @Override
    public boolean isCompleted() {
        return getTxStatusList().stream().allMatch(TransactionExecution::isCompleted);
    }
}
