package cn.vtohru.mysql;

import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.mysql.impl.MysqlDataStore;
import cn.vtohru.orm.DataSourceConfiguration;
import cn.vtohru.orm.DataStore;
import cn.vtohru.orm.DataStoreSpiFactory;
import cn.vtohru.orm.entity.EntityManager;

import javax.inject.Singleton;

@Singleton
public class SqlDataStoreSpiFactory implements DataStoreSpiFactory {

    @Override
    public DataStore createDataStore(VerticleApplicationContext verticleApplicationContext, DataSourceConfiguration dataSourceConfiguration, EntityManager entityManager) {
        return new MysqlDataStore(verticleApplicationContext, dataSourceConfiguration, entityManager);
    }

    @Override
    public boolean accept(String type) {
        return "mysql".equalsIgnoreCase(type);
    }

}
