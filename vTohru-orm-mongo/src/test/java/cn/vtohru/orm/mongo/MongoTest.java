package cn.vtohru.orm.mongo;

import cn.vtohru.orm.dataaccess.write.IWrite;
import cn.vtohru.orm.dataaccess.write.IWriteEntry;
import cn.vtohru.orm.dataaccess.write.IWriteResult;
import cn.vtohru.orm.init.DataStoreSettings;
import cn.vtohru.orm.mongo.dao.UserDao;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.junit.Test;

public class MongoTest {

    @Test
    public void test() {
        JsonObject config = new JsonObject();
        config.put("connection_string", "mongodb://localhost:27017");
        config.put("db_name", "test");
        Vertx vertx = Vertx.vertx();
        MongoClient mongoClient = MongoClient.createShared(vertx, config);
        DataStoreSettings dataStoreSettings = new DataStoreSettings();
        dataStoreSettings.setDatabaseName("PojongoTestDatabase");
        MongoDataStore mongoDataStore = new MongoDataStore(vertx, mongoClient, config, dataStoreSettings);
        UserDao userDao = new UserDao();
        userDao.setName("lingoo4");
        userDao.setAge(20);
        userDao.setGender("F");
        IWrite<UserDao> write = mongoDataStore.createWrite(UserDao.class);
        write.add(userDao);
        write.save(x->{
            if (x.succeeded()) {
                IWriteResult result = x.result();
                for (IWriteEntry iWriteEntry : result) {
                    System.out.println("ID:" + iWriteEntry.getId());
                }
                System.out.println("size: " + result.size());
            } else {
                x.cause().printStackTrace();
            }
        });
        while (true) {
            try {
                Thread.sleep(1000 * 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
