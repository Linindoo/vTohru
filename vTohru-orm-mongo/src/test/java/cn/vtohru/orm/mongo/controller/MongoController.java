package cn.vtohru.orm.mongo.controller;

import cn.vtohru.orm.dataaccess.delete.IDelete;
import cn.vtohru.orm.dataaccess.query.*;
import cn.vtohru.orm.dataaccess.query.exception.VariableSyntaxException;
import cn.vtohru.orm.dataaccess.write.IWrite;
import cn.vtohru.orm.mongo.MongoDataStore;
import cn.vtohru.orm.mongo.dao.ClassDao;
import cn.vtohru.orm.mongo.dao.SchoolDao;
import cn.vtohru.orm.mongo.dao.Status;
import cn.vtohru.orm.transaction.Trans;
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
        IWrite<ClassDao> write = mongoDataStore.createWrite(ClassDao.class);
        Trans trans = mongoDataStore.createTrans();
        IWrite<SchoolDao> schoolDaoIWrite = mongoDataStore.createWrite(SchoolDao.class);
        schoolDaoIWrite.add(schoolDao);
//        write.add(classDao);
        trans.add(schoolDaoIWrite);
//        trans.add(write);
        IDelete<SchoolDao> delete = mongoDataStore.createDelete(SchoolDao.class);
        SchoolDao deleteSchool = new SchoolDao();
        deleteSchool.setId("61c477c3ed056a4b0fb53698");
        delete.add(deleteSchool);
        trans.add(delete);
        trans.commit().onSuccess(x -> {
            System.out.println("success");
            promise.complete();
        }).onFailure(e->{
            e.printStackTrace();
            promise.fail(e);
        });
        promise.complete();
        return promise.future();
    }

    @Path("/list")
    @GET
    public Future<Object> list() {
        Promise<Object> promise = Promise.promise();
        IQuery<ClassDao> query = mongoDataStore.createQuery(ClassDao.class);
        query.execute(new IFieldValueResolver() {
            @Override
            public Object resolve(String variableName) throws VariableSyntaxException {
                return variableName;
            }
        },10,0,x->{
            if (x.succeeded()) {
                IQueryResult<ClassDao> qres = x.result();
                if (qres.size() > 0) {
                    List<ClassDao> result = qres.result();
                    promise.complete(result);
                } else {
                    promise.fail("no date");
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
                        List<ClassDao> result = qres.result();
                        promise.complete(result.get(0));
                    }
                } else {
                    promise.fail(x.cause());
                }
            });
        }
        return promise.future();
    }
}
