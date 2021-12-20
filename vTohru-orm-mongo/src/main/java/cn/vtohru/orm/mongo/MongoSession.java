package cn.vtohru.orm.mongo;

import cn.vtohru.orm.dataaccess.ISession;
import com.mongodb.reactivestreams.client.ClientSession;

public class MongoSession implements ISession {
    private ClientSession clientSession;

    public MongoSession(ClientSession clientSession) {
        this.clientSession = clientSession;
    }

    @Override
    public Object getSession() {
        return this.clientSession;
    }
}
