package cn.vtohru.orm;

import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.orm.entity.EntityManager;

public interface DataStoreSpiFactory {
    DataStore createDataStore(VerticleApplicationContext verticleApplicationContext, DataSourceConfiguration dataSourceConfiguration, EntityManager entityManager);

    boolean accept(String type);
}
