# vTohru

基于 micronaut-ioc 整合了 vertx ，完美解决了 vertx 没有容器的尴尬境地，通过 VerticleScope 保证了在同一个Verticle中bean的唯一性，避免依赖滥用导致的线程安全问题

### 目前实现的功能有
 1. eventbus 远程异步调用
 2. eventbus 消息的注册和消费
 3. web路由的注册
