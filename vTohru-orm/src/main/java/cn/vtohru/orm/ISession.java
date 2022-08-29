package cn.vtohru.orm;

import io.vertx.core.Future;

public interface ISession {
    Future<ClientSession> getSession();
}
