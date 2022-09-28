package cn.vtohru.mongo.controller;

import cn.vtohru.mongo.dao.ClassDao;
import cn.vtohru.mongo.dao.SchoolDao;
import cn.vtohru.mongo.dao.Status;
import cn.vtohru.orm.DataStore;
import cn.vtohru.web.annotation.Controller;
import io.micronaut.core.util.StringUtils;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.Date;

@Controller
@Path("/mongo")
public class MongoController {
    @Inject
    private DataStore mongoDataStore;

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
        classDao.setStatus(Status.ACTIVE);

        SchoolDao schoolDao = new SchoolDao();
        schoolDao.setName("学校");
        SchoolDao deleteSchool = new SchoolDao();
        deleteSchool.setId("61c477c3ed056a4b0fb53698");
        mongoDataStore.onTransaction(t -> {
            t.persist(classDao);
            t.persist(schoolDao);
            t.remove(deleteSchool);
            return Future.succeededFuture();
        }).onSuccess(x -> {
            promise.complete();
        }).onFailure(promise::fail);

        promise.complete();
        return promise.future();
    }

    @Path("/list")
    @GET
    public Future<Object> list() {
        Promise<Object> promise = Promise.promise();
        mongoDataStore.build(ClassDao.class).eq("_id", 1).first().onSuccess(x -> {
            promise.complete(x);
        }).onFailure(promise::fail);
        return promise.future();
    }

    @GET
    @Path("/delete")
    public Future<Object> delete(@QueryParam("id") String id) {
        Promise<Object> promise = Promise.promise();
        if (StringUtils.isEmpty(id)) {
            promise.fail("id不能为空");
        } else {
            ClassDao record = new ClassDao();
            record.setId(id);
            mongoDataStore.remove(record).onSuccess(x -> {
                promise.complete();
            }).onFailure(promise::fail);
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
            ClassDao record = new ClassDao();
            record.setId(id);
            mongoDataStore.fetch(record).onSuccess(x -> {
                x.setName("update_帮技术的开始");
                mongoDataStore.update(x).onSuccess(y -> {
                    promise.complete();
                }).onFailure(promise::fail);
            }).onFailure(promise::fail);
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
            mongoDataStore.build(ClassDao.class).eq("id", id).first().onSuccess(x -> {
                promise.complete(x);
            }).onFailure(promise::fail);
        }
        return promise.future();
    }
}
