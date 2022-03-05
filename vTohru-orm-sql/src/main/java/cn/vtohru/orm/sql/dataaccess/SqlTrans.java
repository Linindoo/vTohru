package cn.vtohru.orm.sql.dataaccess;

import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.transaction.AbstractTrans;
import io.vertx.core.Future;

public class SqlTrans extends AbstractTrans {
    private IDataStore dataStore;

    public SqlTrans(IDataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public Future<Void> commit() {
        return null;
    }
}
