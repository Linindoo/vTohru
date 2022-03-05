package cn.vtohru.orm.sql.dataaccess;

import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.dataaccess.ISession;
import cn.vtohru.orm.dataaccess.write.IWriteResult;
import cn.vtohru.orm.dataaccess.write.impl.AbstractWrite;
import io.vertx.core.Future;

public class SqlWrite<T> extends AbstractWrite<T> {

    public SqlWrite(Class mapperClass, IDataStore datastore) {
        super(mapperClass, datastore);
    }

    @Override
    public Future<Void> execute(ISession session) {
        return null;
    }

    @Override
    protected Future<IWriteResult> internalSave() {
        return null;
    }
}
