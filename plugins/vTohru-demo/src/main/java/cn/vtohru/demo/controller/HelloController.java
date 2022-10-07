package cn.vtohru.demo.controller;

import cn.vtohru.app.api.WeekNotice;
import cn.vtohru.web.annotation.Controller;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@Controller
@Path("/hello")
public class HelloController {

    @Inject
    private List<WeekNotice> weekNoticeList;

    @GET
    @Path("/bye")
    public Future<String> get() {
        Promise<String> promise = Promise.promise();
        if (weekNoticeList != null) {
            for (WeekNotice weekNotice : weekNoticeList) {
                System.out.println(weekNotice.info());
            }
        }
        promise.complete();
        return promise.future();
    }
}
