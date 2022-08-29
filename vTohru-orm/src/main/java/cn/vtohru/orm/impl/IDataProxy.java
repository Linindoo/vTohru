package cn.vtohru.orm.impl;

import cn.vtohru.orm.ClientSession;
import cn.vtohru.orm.ISession;
import io.vertx.core.Future;
import io.vertx.core.Promise;

public class IDataProxy {
    private ISession sessionProxy;
    private ClientSession clientSession;

    public IDataProxy(ISession sessionProxy, ClientSession clientSession) {
        this.sessionProxy = sessionProxy;
        this.clientSession = clientSession;
    }

    public Future<ClientSession> getSession() {
        Promise<ClientSession> promise = Promise.promise();
        if (this.clientSession != null) {
            promise.complete(this.clientSession);
        } else {
            return sessionProxy.getSession();
        }
        return promise.future();
    }
}
