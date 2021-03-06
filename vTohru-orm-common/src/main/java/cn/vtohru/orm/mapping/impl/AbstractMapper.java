package cn.vtohru.orm.mapping.impl;

import cn.vtohru.orm.annotation.Index;
import cn.vtohru.orm.annotation.Indexes;
import cn.vtohru.orm.annotation.KeyGenerator;
import cn.vtohru.orm.annotation.VersionInfo;
import cn.vtohru.orm.dataaccess.query.IIndexedField;
import cn.vtohru.orm.dataaccess.query.IdField;
import cn.vtohru.orm.exception.MappingException;
import cn.vtohru.orm.mapping.*;
import cn.vtohru.orm.mapping.datastore.IColumnHandler;
import cn.vtohru.orm.mapping.datastore.ITableGenerator;
import cn.vtohru.orm.mapping.datastore.ITableInfo;
import cn.vtohru.orm.versioning.IMapperVersion;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.beans.BeanProperty;
import io.vertx.core.*;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * This implementation of {@link IMapper} is using the bean convention to define fields, which shall be mapped. It is
 * first reading all public, non transient fields, then the bean-methods ( public getter/setter ). The way of mapping
 * can be defined by adding several annotations to the field
 *
 * @author Michael Remme
 * @param <T>
 *          the class of the underlaying mapper
 */

public abstract class AbstractMapper<T> implements IMapper<T> {
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMapper.class);
  /**
   * all annotations which shall be examined for the mapper class itself
   */
  protected static final List<Class<? extends Annotation>> CLASS_ANNOTATIONS = Arrays.asList(Indexes.class,
      KeyGenerator.class);

  private final Map<String, IProperty> mappedProperties = new HashMap<>();
  private final Map<Class<? extends Annotation>, IProperty[]> propertyCache = new HashMap<>();
  private final Class<T> mapperClass;
  private final IMapperFactory mapperFactory;
  private IKeyGenerator keyGenerator;
  private IIdInfo idInfo;
  private Entity entity;
  private VersionInfo versionInfo;
  private Set<IIndexDefinition> indexes;
  private ITableInfo tableInfo;
  private boolean syncNeeded = true;
  private BeanIntrospection<T> beanIntrospection;

  /**
   * Class annotations which were found inside the current definition
   */
  private final Map<Class<? extends Annotation>, Annotation> existingClassAnnotations = new HashMap<>();

  /**
   * Methods which are life-cycle events. Per event there can be several methods defined
   */
  private final Map<Class<? extends Annotation>, List<IMethodProxy>> lifecycleMethods = new HashMap<>();

  public AbstractMapper(final Class<T> mapperClass, final IMapperFactory mapperFactory) {
    this.mapperFactory = mapperFactory;
    this.mapperClass = mapperClass;
    init();
  }

  /**
   * Initialize the mapping process
   */
  protected void init() {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("examining " + getMapperClass().getName());
    computePersistentFields();
    computeClassAnnotations();
    computeEntity();
    computeVersionInfo();
    computeKeyGenerator();
    generateTableInfo();
    computeIndexes();
    internalValidate();
  }

  /**
   * Validations, which are not overwritable
   */
  private final void internalValidate() {
    if (idInfo == null)
      throw new MappingException("No id-field specified in mapper " + getMapperClass().getName());
    if (getVersionInfo() != null && !IMapperVersion.class.isAssignableFrom(getMapperClass())) {
      throw new MappingException(
          "Mapper, where the property Entity.version is set must implement the interface IMapperVersion");
    }
    validate();
  }
  /**
   * Validation for required properties etc
   */
  protected abstract void validate();

  /**
   * Compute all fields, which shall be persisted
   */
  protected void computePersistentFields(){
    computeFieldProperties();
  }

    /**
     * Computes the properties from the public fields of the class, which are not transient
     */
  public void computeFieldProperties() {
    this.beanIntrospection = BeanIntrospection.getIntrospection(getMapperClass());
    for (BeanProperty<T, ?> beanProperty : beanIntrospection.getBeanProperties()) {
      String name = beanProperty.getName();
      DefaultMappedField<T> mf = new DefaultMappedField(beanProperty, this);
      this.saveMappedField(name, mf);
    }
  }

  protected void saveMappedField(final String name, IProperty mf) {
    if (mf.hasAnnotation(Id.class)) {
      if (getIdInfo() != null)
        throw new MappingException("duplicate Id field definition found for mapper " + getMapperClass());
      setIdInfo(new IdInfo(mf));
    }
    if (!mf.isIgnore()) {
      this.getMappedProperties().put(name, mf);
    }
  }


  protected void generateTableInfo() {
    if (getMapperFactory().getDataStore() != null) {
      ITableGenerator tg = getMapperFactory().getDataStore().getTableGenerator();
      this.tableInfo = tg.createTableInfo(this);
      for (String fn : getFieldNames()) {
        IProperty field = getField(fn);
        IColumnHandler ch = tg.getColumnHandler(field);
        this.tableInfo.createColumnInfo(field, ch);
      }
    }
  }

  protected void computeVersionInfo() {
    if (mapperClass.isAnnotationPresent(VersionInfo.class)) {
      versionInfo = mapperClass.getAnnotation(VersionInfo.class);
    }
  }

  protected void computeEntity() {
    if (mapperClass.isAnnotationPresent(Entity.class)) {
      entity = mapperClass.getAnnotation(Entity.class);
    }
  }

  protected void computeIndexes() {
    Map<String, IIndexDefinition> definitions = new HashMap<>();
    if (getMapperClass().isAnnotationPresent(Indexes.class)) {
      Indexes tmpIndexes = getMapperClass().getAnnotation(Indexes.class);
      for (Index index : tmpIndexes.value()) {
        IndexDefinition indexDefinition = new IndexDefinition(index);
        IIndexDefinition old = definitions.put(indexDefinition.getIdentifier(), indexDefinition);
        if (old != null) {
          throw new IllegalStateException("duplicate index definition:" + indexDefinition);
        }
      }
    }
    computeIndexes(definitions);
    this.indexes = new HashSet<>(definitions.values());
  }

  /**
   * @param definitions
   */
  private void computeIndexes(final Map<String, IIndexDefinition> definitions) {
    Collection<BeanProperty<T, Object>> beanProperties = beanIntrospection.getBeanProperties();
    for (BeanProperty<T, Object> beanProperty : beanProperties) {
      computeIndexByField(definitions, beanProperty);
    }
  }

  /**
   * @param definitions
   * @param field
   */
  private void computeIndexByField(final Map<String, IIndexDefinition> definitions, final BeanProperty<T, Object> field) {
    if (IIndexedField.class.isAssignableFrom(field.getType())
        && !IdField.class.isAssignableFrom(field.getType())) {
      IIndexedField indexedField = (IIndexedField) field.get(null);
      IndexDefinition indexDefinition = new IndexDefinition(indexedField, this);
      if (definitions.containsKey(indexDefinition.getIdentifier())) {
        assert indexDefinition.getIndexOptions()
            .isEmpty() : "if indexed fields define index options, incompatibility must be checked here";
        LOGGER
            .info("Didn't add index definition because there already is one for its identifier: " + indexDefinition);
      } else{
        definitions.put(indexDefinition.getIdentifier(), indexDefinition);
      }
    }
  }

  protected void computeKeyGenerator() {
    if (getMapperFactory().getDataStore() != null) {
      KeyGenerator gen = getAnnotation(KeyGenerator.class);
      if (gen != null) {
        String name = gen.value();
        keyGenerator = getMapperFactory().getDataStore().getKeyGenerator(name);
      } else {
        keyGenerator = getMapperFactory().getDataStore().getDefaultKeyGenerator();
      }
    }
  }

  protected final void computeClassAnnotations() {
    for (Class<? extends Annotation> annClass : CLASS_ANNOTATIONS) {
      Annotation ann = mapperClass.getAnnotation(annClass);
      if (ann != null)
        existingClassAnnotations.put(annClass, ann);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.mapping.IMapper#getFieldNames()
   */
  @Override
  public Set<String> getFieldNames() {
    return this.mappedProperties.keySet();
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.mapping.IMapper#getField(java.lang.String)
   */
  @Override
  public IProperty getField(final String name) {
    IProperty field = this.mappedProperties.get(name);
    if (field == null)
      throw new cn.vtohru.orm.exception.NoSuchFieldException(this, name);
    return field;
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.mapping.IMapper#getAnnotatedFields(java.lang.Class)
   */
  @Override
  public IProperty[] getAnnotatedFields(final Class<? extends Annotation> annotationClass) {
    if (!this.propertyCache.containsKey(annotationClass)) {
      IProperty[] result = new IProperty[0];
      for (IProperty field : this.mappedProperties.values()) {
        if (field.getAnnotation(annotationClass) != null) {
          IProperty[] newArray = new IProperty[result.length + 1];
          System.arraycopy(result, 0, newArray, 0, result.length);
          result = newArray;
          result[result.length - 1] = field;
        }
      }
      this.propertyCache.put(annotationClass, result);
    }
    return this.propertyCache.get(annotationClass);
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.mapping.IMapper#executeLifecycle(java.lang.Class, java.lang.Object)
   */
  @Override
  public void executeLifecycle(final Class<? extends Annotation> annotationClass, final T entity,
      final Handler<AsyncResult<Void>> handler) {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("start executing Lifecycle " + annotationClass.getSimpleName());
    List<IMethodProxy> methods = getLifecycleMethods(annotationClass);
    if (methods == null || methods.isEmpty()) {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("nothing to execute");
      handler.handle(Future.succeededFuture());
    } else {
      executeLifecycleMethods(entity, handler, methods);
    }
  }

  /**
   * @param entity
   * @param handler
   * @param methods
   */
  private void executeLifecycleMethods(final Object entity, final Handler<AsyncResult<Void>> handler,
      final List<IMethodProxy> methods) {
    CompositeFuture cf = CompositeFuture.all(createFutureList(entity, methods));
    cf.onComplete(res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        handler.handle(Future.succeededFuture());
      }
    });
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private List<Future> createFutureList(final Object entity, final List<IMethodProxy> methods) {
    List<Future> fl = new ArrayList<>();
    for (IMethodProxy mp : methods) {
      Promise<Void> promise = Promise.promise();
      if (LOGGER.isDebugEnabled())
        LOGGER
            .debug("execute lifecycle method: " + getMapperClass().getSimpleName() + " - " + mp.getMethod().getName());
      executeMethod(mp, entity, promise);
      fl.add(promise.future());
    }
    return fl;
  }

  /**
   * Execute the trigger method. IMPORTANT: if a TriggerContext is created, the handler is informed by the
   * TriggerContext, if not, then the handler is informed by this method
   *
   * @param mp
   * @param entity
   * @param handler
   */
  private void executeMethod(final IMethodProxy mp, final Object entity, final Handler<AsyncResult<Void>> handler) {
    Method method = mp.getMethod();
    method.setAccessible(true);
    Object[] args = mp.getParameterTypes() == null ? null
        : new Object[] {
            getMapperFactory().getDataStore().getTriggerContextFactory().createTriggerContext(this, handler) };
    try {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("invoking trigger method " + getMapperClass().getSimpleName() + " - " + method.getName());
      method.invoke(entity, args);
      if (args == null) {
        // ONLY INFORM HANDLER, if no TriggerContext is given
        handler.handle(Future.succeededFuture());
      }
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("trigger method invokement finished");
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  protected final void addLifecycleAnnotationMethod(final Class<? extends Annotation> ann, final Method method) {
    List<IMethodProxy> lcMethods = lifecycleMethods.get(ann);
    if (lcMethods == null) {
      lcMethods = new ArrayList<>();
      lifecycleMethods.put(ann, lcMethods);
    }
    MethodProxy mp = new MethodProxy(method, this);
    if (!lcMethods.contains(mp)) {
      lcMethods.add(mp);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.mapping.IMapper#getMapperClass()
   */
  @Override
  public final Class<T> getMapperClass() {
    return mapperClass;
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.mapping.IMapper#getLifecycleMethods(java.lang.Class)
   */
  @Override
  public final List<IMethodProxy> getLifecycleMethods(final Class<? extends Annotation> annotation) {
    if (lifecycleMethods.isEmpty())
      return null;
    return lifecycleMethods.get(annotation);
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.mapping.IMapper#getAnnotation(java.lang.Class)
   */
  @Override
  public <U extends Annotation> U getAnnotation(final Class<U> annotationClass) {
    return beanIntrospection.getBeanType().getAnnotation(annotationClass);
  }

  @Override
  public ITableInfo getTableInfo() {
    return tableInfo;
  }

  @Override
  public final IIdInfo getIdInfo() {
    return idInfo;
  }

  protected void setIdInfo(final IIdInfo idInfo) {
    this.idInfo = idInfo;
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.mapping.IMapper#getKeyGenerator()
   */
  @Override
  public IKeyGenerator getKeyGenerator() {
    return keyGenerator;
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.mapping.IMapper#isSyncNeeded()
   */
  @Override
  public final boolean isSyncNeeded() {
    return syncNeeded;
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.mapping.IMapper#setSyncNeeded(boolean)
   */
  @Override
  public final void setSyncNeeded(final boolean syncNeeded) {
    this.syncNeeded = syncNeeded;
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.mapping.IMapper#getEntity()
   */
  @Override
  public Entity getEntity() {
    return this.entity;
  }

  @Override
  public VersionInfo getVersionInfo() {
    return versionInfo;
  }

  @Override
  public Set<IIndexDefinition> getIndexDefinitions() {
    return indexes;
  }

  @Override
  public IMapperFactory getMapperFactory() {
    return this.mapperFactory;
  }

  protected Map<String, IProperty> getMappedProperties() {
    return mappedProperties;
  }

}
