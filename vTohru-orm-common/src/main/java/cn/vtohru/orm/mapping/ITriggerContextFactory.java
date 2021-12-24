package cn.vtohru.orm.mapping;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
@FunctionalInterface
public interface ITriggerContextFactory {

  /**
   * Creates a new instance of {@link ITriggerContext} before a defined lifecycle method is called
   * 
   * @param mapper
   *          the mapper, which is handled
   * @param handler
   *          the handler to be informed
   * 
   * @return
   */
  ITriggerContext createTriggerContext(IMapper mapper, Handler<AsyncResult<Void>> handler);
}
