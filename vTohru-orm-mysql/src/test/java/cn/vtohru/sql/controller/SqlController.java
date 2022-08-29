package cn.vtohru.sql.controller;

import cn.vtohru.orm.DataStore;
import cn.vtohru.sql.dao.Author;
import cn.vtohru.sql.dao.News;
import cn.vtohru.web.annotation.Controller;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/sql")
@Controller
public class SqlController {
    @Inject
    @Named("mysql")
    private DataStore dataStore;

    @Path("/add")
    @GET
    public Future<Object> addNews() {
        Promise<Object> promise = Promise.promise();
        News news = new News();
        news.setContent("hello");
        news.setTitle("标题");
        dataStore.insert(news).onSuccess(x -> {
            System.out.println(x.getId());
            promise.complete(x.getId());
        }).onFailure(promise::fail);
        return promise.future();
    }

    @Path("/update")
    @GET
    public Future<Object> update(@QueryParam("id") Long id) {
        Promise<Object> promise = Promise.promise();
        News model = new News();
        model.setId(id);
        dataStore.fetch(model).onSuccess(x -> {
            System.out.println(x.getTitle());
            x.setTitle("23333");
            dataStore.update(x).onSuccess(y -> {
                System.out.println(y.getTitle());
                promise.complete(y.getTitle());
            }).onFailure(promise::fail);
        }).onFailure(promise::fail);
        return promise.future();
    }

    @Path("/delete")
    @GET
    public Future<Object> delete(@QueryParam("id") Long id) {
        Promise<Object> promise = Promise.promise();
        News model = new News();
        model.setId(id);
        dataStore.remove(model).onSuccess(x -> {
            promise.complete("ok");
        }).onFailure(promise::fail);
        return promise.future();
    }


    @Path("/query")
    @GET
    public Future<Object> query() {
        Promise<Object> promise = Promise.promise();
        String jpql = dataStore.build(News.class).eq("id", 1).and(x -> {
            x.eq("title", "2333");
        }).getJpql();
        System.out.println(jpql);
        promise.complete(jpql);
        return promise.future();
    }

    @Path("/trans")
    @GET
    public Future<Object> trans() {
        Promise<Object> promise = Promise.promise();
        dataStore.onTransaction(clientSession -> {
            Promise<String> trans = Promise.promise();
            News news = new News();
            news.setTitle("重大新闻");
            news.setContent("明天再说");
            clientSession.persist(news).onSuccess(x -> {
                Author author = new Author();
                author.setName("朱丽叶");
                author.setAge(10);
                clientSession.persist(author).onSuccess(y -> {
                    trans.complete(y.getName());
                }).onFailure(promise::fail);
            }).onFailure(promise::fail);
            return trans.future();
        }).onSuccess(x -> {
            System.out.println(x);
            promise.complete(x);
        }).onFailure(promise::fail);
        return promise.future();
    }
}
