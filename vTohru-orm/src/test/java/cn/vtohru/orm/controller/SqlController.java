package cn.vtohru.orm.controller;

import cn.vtohru.orm.DataStore;
import cn.vtohru.web.annotation.Controller;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/orm")
@Controller
public class SqlController {
    @Inject
    private DataStore sqlDataStore;

    @Path("/test")
    @GET
    public Future<Object> test() {
        Promise<Object> promise = Promise.promise();
        return promise.future();
    }
}
