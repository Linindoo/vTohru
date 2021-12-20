package cn.vtohru.orm.transaction;

import cn.vtohru.orm.dataaccess.IDataAccessObject;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTrans implements Trans {
    protected List<IDataAccessObject<?>> dataAccessObjects = new ArrayList<>();

    @Override
    public <T> Trans add(IDataAccessObject<T> dataAccessObject) {
        dataAccessObjects.add(dataAccessObject);
        return this;
    }

    @Override
    public List<IDataAccessObject<?>> getDataAccess() {
        return this.dataAccessObjects;
    }
}
