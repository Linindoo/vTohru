package cn.vtohru.orm;

import cn.vtohru.annotation.Verticle;
import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.orm.entity.EntityManager;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;

@Factory
public class DataStoreFactory {
    private static final Logger logger = LoggerFactory.getLogger(DataStoreFactory.class);
    @Inject
    private EntityManager entityManager;

    @EachBean(DataSourceConfiguration.class)
    @Verticle
    DataStore dataSource(ApplicationContext applicationContext, DataSourceConfiguration configuration) {
        Collection<DataStoreSpiFactory> spiFactories = applicationContext.getBeansOfType(DataStoreSpiFactory.class);
        for (DataStoreSpiFactory dataStoreSpiFactory : spiFactories) {
            if (dataStoreSpiFactory.accept(configuration.getType())) {
                return dataStoreSpiFactory.createDataStore((VerticleApplicationContext) applicationContext, configuration, entityManager);
            }
        }
        logger.error("no datasource type : " + configuration.getType() + " fetch");
        return null;
    }

}
