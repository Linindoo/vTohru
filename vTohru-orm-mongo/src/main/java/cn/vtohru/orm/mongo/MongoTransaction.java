package cn.vtohru.orm.mongo;

import cn.vtohru.orm.dataaccess.IDataAccessObject;
import cn.vtohru.orm.dataaccess.delete.IDelete;
import cn.vtohru.orm.dataaccess.write.IWrite;
import cn.vtohru.orm.transaction.AbstractTrans;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.reactivestreams.client.MongoClient;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxInternal;
import io.vertx.ext.mongo.impl.CompletionSubscriber;

import java.util.ArrayList;
import java.util.List;


public class MongoTransaction extends AbstractTrans {
    private MongoClient mongoClient;
    private MongoDataStore mongoDataStore;
    private VertxInternal vertx;

    public MongoTransaction(MongoDataStore mongoDataStore, Vertx vertx) {
        this.mongoDataStore = mongoDataStore;
        this.vertx = (VertxInternal) vertx;
    }

    @Override
    public Future<Void> commit() {
        TransactionOptions txnOptions = TransactionOptions.builder()
                .readPreference(ReadPreference.primary())
                .readConcern(ReadConcern.MAJORITY)
                .writeConcern(WriteConcern.MAJORITY)
                .build();
        io.vertx.ext.mongo.MongoClient mongoDataStoreClient = (io.vertx.ext.mongo.MongoClient) mongoDataStore.getClient();
        Promise<Void> commitPromise = Promise.promise();
        mongoDataStoreClient.startSession().onSuccess(x -> {
            x.startTransaction(txnOptions);
            try {
                List<Future> transFutures = new ArrayList<>();
                for (IDataAccessObject<?> dataAccess : getDataAccess()) {
                    MongoSession session = new MongoSession(x);
                    dataAccess.setSession(session);
                    if (dataAccess instanceof IDelete) {
                        IDelete delete = (IDelete) dataAccess;
                        transFutures.add(delete.execute(session));
                    } else if (dataAccess instanceof IWrite) {
                        IWrite write = (IWrite) dataAccess;
                        transFutures.add(write.execute(session));
                    }
                }
                CompositeFuture.all(transFutures).onComplete(y -> {
                    Promise<Void> endPromise = Promise.promise();
                    if (y.succeeded()) {
                        x.commitTransaction().subscribe(new CompletionSubscriber<>(endPromise));
                    } else {
                        x.abortTransaction().subscribe(new CompletionSubscriber<>(endPromise));
                    }
                    endPromise.future().onComplete(t -> {
                        x.close();
                        if (y.succeeded()) {
                            commitPromise.complete();
                        } else {
                            commitPromise.fail(y.cause());
                        }
                    });
                });
            } catch (Exception e) {
                x.abortTransaction();
                x.close();
            }
        }).onFailure(commitPromise::fail);
        return commitPromise.future();
    }

}
