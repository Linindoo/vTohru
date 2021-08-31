package cn.vtohru.orm.mongo.controller;

import cn.vtohru.orm.dataaccess.write.IWrite;
import cn.vtohru.orm.dataaccess.write.IWriteEntry;
import cn.vtohru.orm.dataaccess.write.IWriteResult;
import cn.vtohru.orm.mongo.MongoDataStore;
import cn.vtohru.orm.mongo.dao.ClassDao;
import cn.vtohru.web.annotation.Controller;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Controller
@Path("/mongo")
public class MongoController {
    @Inject
    private MongoDataStore mongoDataStore;

    @Path("/add")
    @GET
    public Future<Object> add() {
        Promise<Object> promise = Promise.promise();
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
                promise.complete(result.size());
            } else {
                promise.fail(x.cause());
                x.cause().printStackTrace();
            }
        });
        return promise.future();
    }
}
