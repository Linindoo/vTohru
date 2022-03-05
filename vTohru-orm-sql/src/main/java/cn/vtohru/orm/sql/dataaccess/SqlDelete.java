package cn.vtohru.orm.sql.dataaccess;

import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.dataaccess.ISession;
import cn.vtohru.orm.dataaccess.delete.IDeleteResult;
import cn.vtohru.orm.dataaccess.delete.impl.Delete;
import cn.vtohru.orm.dataaccess.query.IQuery;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public class SqlDelete<T> extends Delete<T> {
    /**
     * @param mapperClass
     * @param datastore
     */
    public SqlDelete(Class<T> mapperClass, IDataStore datastore) {
        super(mapperClass, datastore);
    }

    @Override
    public Future<Void> execute(ISession session) {
        return null;
    }

    @Override
    protected void deleteQuery(IQuery<T> query, Handler<AsyncResult<IDeleteResult>> resultHandler) {

    }
}
