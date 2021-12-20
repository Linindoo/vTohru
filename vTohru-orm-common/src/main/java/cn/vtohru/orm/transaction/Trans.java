package cn.vtohru.orm.transaction;

import cn.vtohru.orm.dataaccess.IDataAccessObject;
import io.vertx.core.Future;

import java.util.List;

public interface Trans {

    <T> Trans add(IDataAccessObject<T> dataAccessObject);

    Future<Void> commit();

    List<IDataAccessObject<?>> getDataAccess();
}
