package cn.vtohru.mongo.impl;

import cn.vtohru.orm.ITransaction;
import com.mongodb.reactivestreams.client.ClientSession;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.BulkOperation;
import io.vertx.ext.mongo.BulkOperationType;
import io.vertx.ext.mongo.impl.CompletionSubscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoTransaction implements ITransaction {
    private ClientSession clientSession;
    private boolean closed = false;
    private Map<String, List<BulkOperation>> bulkMap = new HashMap<>();

    public MongoTransaction(ClientSession clientSession) {
        this.clientSession = clientSession;
    }

    @Override
    public Future<Void> commit() {
        Promise<Void> promise = Promise.promise();
        clientSession.commitTransaction().subscribe(new CompletionSubscriber<>(promise));
        this.closed = true;
        return promise.future();
    }

    @Override
    public Future<Void> rollback() {
        Promise<Void> promise = Promise.promise();
        clientSession.abortTransaction().subscribe(new CompletionSubscriber<>(promise));
        this.closed = true;
        return promise.future();
    }

    public boolean isClosed() {
        return this.closed;
    }

    public ClientSession getClientSession() {
        return clientSession;
    }
}
