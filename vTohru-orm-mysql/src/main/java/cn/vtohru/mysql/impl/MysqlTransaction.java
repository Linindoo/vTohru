package cn.vtohru.mysql.impl;

import cn.vtohru.orm.ITransaction;
import io.vertx.core.Future;
import io.vertx.sqlclient.Transaction;

public class MysqlTransaction implements ITransaction {
    private Transaction transaction;

    public MysqlTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public Future<Void> commit() {
        return transaction.commit();
    }

    @Override
    public Future<Void> rollback() {
        return transaction.rollback();
    }

}
