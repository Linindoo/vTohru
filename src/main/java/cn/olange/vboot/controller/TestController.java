package cn.olange.vboot.controller;

import cn.olange.vboot.annotation.Controller;
import cn.olange.vboot.microservice.AsyncService;
import cn.olange.vboot.microservice.Client;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.servicediscovery.types.MongoDataSource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;


@Controller
public class TestController {

    @Inject
    @Client(type = MongoDataSource.TYPE)
    private AsyncService<MongoClient> mongoClientAsyncService;

    @GET
    @Path("/hello")
    public String hello() {
        mongoClientAsyncService.get();
        return "world";
    }
}
