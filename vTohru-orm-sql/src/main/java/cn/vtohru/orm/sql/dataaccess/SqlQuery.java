package cn.vtohru.orm.sql.dataaccess;

import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.dataaccess.query.impl.IQueryExpression;
import cn.vtohru.orm.dataaccess.query.impl.Query;
import io.vertx.core.Handler;

public class SqlQuery<T> extends Query<T> {

    public SqlQuery(Class mapperClass, IDataStore datastore) {
        super(mapperClass, datastore);
    }

    @Override
    public void executeExplain(Handler handler) {

    }

    @Override
    protected void internalExecute(IQueryExpression queryExpression, Handler handler) {

    }

    @Override
    protected void internalExecuteCount(IQueryExpression queryExpression, Handler handler) {

    }

    @Override
    protected Class<? extends IQueryExpression> getQueryExpressionClass() {
        return null;
    }
}
