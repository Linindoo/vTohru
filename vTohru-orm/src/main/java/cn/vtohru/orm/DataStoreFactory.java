package cn.vtohru.orm;

import cn.vtohru.annotation.Verticle;
import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.orm.entity.EntityManager;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.convert.ConversionService;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

@Factory
public class DataStoreFactory {
    private static final Logger logger = LoggerFactory.getLogger(DataStoreFactory.class);
    private EntityManager entityManager;

    public DataStoreFactory(EntityManager entityManager) {
        this.entityManager = entityManager;
        ConversionService.SHARED.addConverter(Long.class, Date.class, (object, targetType, context1) -> Optional.of(new Date(object)));
        ConversionService.SHARED.addConverter(JsonObject.class, Object.class, (jsonObject, targetType, context2) -> Optional.of(jsonObject.mapTo(targetType)));
    }

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
