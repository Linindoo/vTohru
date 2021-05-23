package cn.olange.vboot.microservice;

import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;


public class AsyncServiceImpl<T> extends AsyncService<T> {
    private static final Logger logger = LoggerFactory.getLogger(AsyncServiceImpl.class);
    protected MicroServiceDiscovery discovery;
    private final JsonObject config;

    public AsyncServiceImpl(MicroServiceDiscovery discovery, JsonObject config) {
        this.discovery = discovery;
        this.config = config;
    }

    @Override
    public Promise<T> get() {
        Promise<T> promise = Promise.promise();
        logger.info("获取服务:" + this.config.toString());
        discovery.getRecord(this.config).onSuccess(reference -> {
            if (reference == null) {
                promise.fail("未匹配到合适的服务");
            } else {
                if (!reference.record().getRegistration().equalsIgnoreCase(this.getRegistration())) {
                    logger.info(this.config.toString() + ":服务ID改变了:" + getRegistration() + ">" + reference.record().getRegistration());
                    this.setRegistration(reference.record().getRegistration());
                }
//                if (end != null) {
//                    end.future().compose(x -> {
//                        logger.info("服务释放:" + this.config.toString());
//                        reference.release();
//                        return Future.succeededFuture(x);
//                    }, e -> {
//                        logger.info("服务释放:" + this.config.toString());
//                        reference.release();
//                        return Future.failedFuture(e);
//                    });
//                } else {
//                    logger.warn("当前回调promise为空");
//                }
                promise.complete(reference.get());
            }
        }).onFailure(promise::fail);
        return promise;
    }
}
