package cn.vtohru.orm.data;

import cn.vtohru.orm.DbSession;
import cn.vtohru.orm.ISession;
import io.vertx.core.Future;
import io.vertx.core.Promise;

public class IDataProxy {
    private ISession sessionProxy;
    private DbSession dbSession;

    public IDataProxy(ISession sessionProxy, DbSession dbSession) {
        this.sessionProxy = sessionProxy;
        this.dbSession = dbSession;
    }

    public Future<DbSession> getSession() {
        Promise<DbSession> promise = Promise.promise();
        if (this.dbSession != null) {
            promise.complete(this.dbSession);
        } else {
            return sessionProxy.getSession();
        }
        return promise.future();
    }
}
