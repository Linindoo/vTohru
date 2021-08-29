package cn.vtohru.orm.mongo;

import cn.vtohru.orm.dataaccess.delete.IDelete;
import cn.vtohru.orm.dataaccess.write.IWrite;
import cn.vtohru.orm.dataaccess.write.IWriteEntry;
import cn.vtohru.orm.dataaccess.write.IWriteResult;
import cn.vtohru.orm.init.DataStoreSettings;
import cn.vtohru.orm.mongo.dao.ClassDao;
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
//        BulkOperation insert = BulkOperation.createInsert(new JsonObject().put("name", "sb1").put("sex", 1));
//        BulkOperation insert2 = BulkOperation.createReplace(new JsonObject().put("_id", "6129f724af7a9a4e45d7ed29"), new JsonObject().put("name", "sb2").put("sex", 1));
//        List<BulkOperation> bulkOperations = new ArrayList<>();
//        bulkOperations.add(insert);
//        bulkOperations.add(insert2);
//        mongoClient.bulkWrite("demo", bulkOperations).onSuccess(x -> {
//            System.out.println(x.toJson());
//        }).onFailure(Throwable::printStackTrace);
        DataStoreSettings dataStoreSettings = new DataStoreSettings();
        dataStoreSettings.setDatabaseName("PojongoTestDatabase");
        MongoDataStore mongoDataStore = new MongoDataStore(vertx, mongoClient, config, dataStoreSettings);
        ClassDao userDao = new ClassDao();
        userDao.setName("班级1");
        IWrite<ClassDao> write = mongoDataStore.createWrite(ClassDao.class);
        write.add(userDao);
        ClassDao update = new ClassDao();
        update.setName("班级_update");
        update.setId("612a2d20dde6ff1dca4dfee3");
        write.add(update);
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
        IDelete<ClassDao> delete = mongoDataStore.createDelete(ClassDao.class);
        ClassDao deleteDao = new ClassDao();
        deleteDao.setId("6129f2d51779061eea37b29f");
        delete.add(deleteDao);
        delete.delete(x->{
            if (x.succeeded()) {
                System.out.println(x.result().getDeletedInstances());
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
