package cn.vtohru.orm.sql.controller;

import cn.vtohru.orm.sql.SqlDataStore;
import cn.vtohru.web.annotation.Controller;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/sql")
@Controller
public class SqlController {
    @Inject
    private SqlDataStore sqlDataStore;

    @Path("/add")
    @GET
    public Future<Object> addNews() {
        Promise<Object> promise = Promise.promise();

        return promise.future();
    }
}
