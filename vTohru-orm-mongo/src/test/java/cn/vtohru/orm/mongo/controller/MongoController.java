package cn.vtohru.orm.mongo.controller;

import cn.vtohru.orm.dataaccess.delete.IDelete;
import cn.vtohru.orm.dataaccess.query.IQuery;
import cn.vtohru.orm.dataaccess.query.IQueryResult;
import cn.vtohru.orm.dataaccess.query.ISearchCondition;
import cn.vtohru.orm.dataaccess.write.IWrite;
import cn.vtohru.orm.dataaccess.write.IWriteEntry;
import cn.vtohru.orm.dataaccess.write.IWriteResult;
import cn.vtohru.orm.mongo.MongoDataStore;
import cn.vtohru.orm.mongo.dao.ClassDao;
import cn.vtohru.orm.mongo.dao.SchoolDao;
import cn.vtohru.orm.mongo.dao.Status;
import cn.vtohru.web.annotation.Controller;
import io.micronaut.core.util.StringUtils;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@Path("/mongo")
public class MongoController {
    @Inject
    private MongoDataStore mongoDataStore;

    @Path("/add")
    @GET
    public Future<Object> add() {
        Promise<Object> promise = Promise.promise();
        ClassDao classDao = new ClassDao();
        classDao.setName("班级1_hello");
        classDao.setMax(100L);
        classDao.setEnable(true);
        classDao.setNumber(10.8);
        classDao.setCreateTime(new Date());
        classDao.setStudentNum(77);
        classDao.setMin(8.90F);
        classDao.setTags(Arrays.asList("one", "two"));
        classDao.setStatus(Status.ACTIVE);
        SchoolDao schoolDao = new SchoolDao();
        schoolDao.setName("学校");
        schoolDao.setId(UUID.randomUUID().toString());
        classDao.setSchoolDao(schoolDao);
        IWrite<ClassDao> write = mongoDataStore.createWrite(ClassDao.class);
        write.add(classDao);
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

    @Path("/list")
    @GET
    public Future<Object> list() {
        Promise<Object> promise = Promise.promise();
        IQuery<ClassDao> query = mongoDataStore.createQuery(ClassDao.class);
        query.execute(x->{
            if (x.succeeded()) {
                IQueryResult<ClassDao> qres = x.result();
                if (qres.size() > 0) {
                    qres.iterator().result(y->{
                        if (y.succeeded()) {
                            List<ClassDao> classDaos = y.result();
                            promise.complete(classDaos);
                        } else {
                            promise.fail(y.cause());
                        }
                    });
                }
            } else {
                promise.fail(x.cause());
            }
        });
        return promise.future();
    }

    @GET
    @Path("/delete")
    public Future<Object> delete(@QueryParam("id") String id) {
        Promise<Object> promise = Promise.promise();
        if (StringUtils.isEmpty(id)) {
            promise.fail("id不能为空");
        } else {
            IDelete<ClassDao> delete = mongoDataStore.createDelete(ClassDao.class);
            ClassDao record = new ClassDao();
            record.setId(id);
            delete.add(record);
            delete.delete(x->{
                if (x.succeeded()) {
                    promise.complete(x.result().getDeletedInstances());
                } else {
                    x.cause().printStackTrace();
                    promise.fail(x.cause());
                }
            });
        }
        return promise.future();
    }

    @GET
    @Path("/update")
    public Future<Object> update(@QueryParam("id") String id) {
        Promise<Object> promise = Promise.promise();
        if (StringUtils.isEmpty(id)) {
            promise.fail("id不能为空");
        } else {
            IWrite<ClassDao> write = mongoDataStore.createWrite(ClassDao.class);
            ClassDao record = new ClassDao();
            record.setId(id);
            record.setName("update_帮技术的开始");
            write.add(record);
            write.save(x->{
                if (x.succeeded()) {
                    promise.complete(x.result().size());
                } else {
                    x.cause().printStackTrace();
                    promise.fail(x.cause());
                }
            });
        }
        return promise.future();
    }

    @GET
    @Path("/get")
    public Future<Object> get(@QueryParam("id") String id) {
        Promise<Object> promise = Promise.promise();
        if (StringUtils.isEmpty(id)) {
            promise.fail("id不能为空");
        } else {
            IQuery<ClassDao> query = mongoDataStore.createQuery(ClassDao.class);
            query.setSearchCondition(ISearchCondition.isEqual("id", id));
            query.execute(x->{
                if (x.succeeded()) {
                    IQueryResult<ClassDao> qres = x.result();
                    if (qres.size() > 0) {
                        qres.iterator().next(y->{
                            if (y.succeeded()) {
                                ClassDao classDaos = y.result();
                                promise.complete(classDaos);
                            } else {
                                y.cause().printStackTrace();
                                promise.fail(y.cause());
                            }
                        });
                    }
                } else {
                    promise.fail(x.cause());
                }
            });
        }
        return promise.future();
    }
}
