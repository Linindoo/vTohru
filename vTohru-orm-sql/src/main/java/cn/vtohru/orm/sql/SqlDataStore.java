package cn.vtohru.orm.sql;

import cn.vtohru.annotation.GlobalScope;
import cn.vtohru.annotation.Verticle;
import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.orm.dataaccess.delete.IDelete;
import cn.vtohru.orm.dataaccess.query.IQuery;
import cn.vtohru.orm.dataaccess.write.IWrite;
import cn.vtohru.orm.impl.AbstractDataStore;
import cn.vtohru.orm.init.DataStoreSettings;
import cn.vtohru.orm.mapping.IKeyGenerator;
import cn.vtohru.orm.sql.dataaccess.SqlDelete;
import cn.vtohru.orm.sql.dataaccess.SqlQuery;
import cn.vtohru.orm.sql.dataaccess.SqlTrans;
import cn.vtohru.orm.sql.dataaccess.SqlWrite;
import cn.vtohru.orm.transaction.Trans;
import io.micronaut.context.ApplicationContext;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnectOptions;

@Verticle
@GlobalScope
public class SqlDataStore extends AbstractDataStore<JsonObject, JsonObject> {
    private static final String SQL_CONFIG_KEY = "sql";
    private static final String POOL_CONFIG_KEY = "pool";
    private SqlClient clientPool;
    private VerticleApplicationContext context;

    public SqlDataStore(ApplicationContext context) {
        super((VerticleApplicationContext)context, new JsonObject(), new DataStoreSettings());
        this.context = (VerticleApplicationContext) context;
        JsonObject sqlConfig = this.context.getVProperty(SQL_CONFIG_KEY, JsonObject.class).orElse(new JsonObject());
        SqlConnectOptions database = new SqlConnectOptions(sqlConfig);
        JsonObject poolConfig = this.context.getVProperty(POOL_CONFIG_KEY, JsonObject.class).orElse(new JsonObject());
        PoolOptions options = new PoolOptions(poolConfig);
        this.clientPool = Pool.pool(this.context.getVertx(), database, options);
    }

    @Override
    public <T> IQuery<T> createQuery(Class<T> mapper) {
        return new SqlQuery<>(mapper, this);
    }

    @Override
    public <T> IWrite<T> createWrite(Class<T> mapper) {
        return new SqlWrite(mapper,this);
    }

    @Override
    public <T> IDelete<T> createDelete(Class<T> mapper) {
        return new SqlDelete(mapper, this);
    }

    @Override
    public IKeyGenerator getDefaultKeyGenerator() {
        return null;
    }

    @Override
    public void shutdown(Handler<AsyncResult<Void>> resultHandler) {

    }

    @Override
    public Object getClient() {
        return this.clientPool;
    }

    @Override
    public Trans createTrans() {
        return new SqlTrans(this);
    }
}
