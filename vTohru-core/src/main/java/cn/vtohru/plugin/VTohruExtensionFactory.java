package cn.vtohru.plugin;

import cn.vtohru.context.VerticleApplicationContext;
import org.pf4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

public class VTohruExtensionFactory implements ExtensionFactory {

    private static final Logger log = LoggerFactory.getLogger(VTohruExtensionFactory.class);
    public static final boolean AUTOWIRE_BY_DEFAULT = true;
    /**
     * The plugin manager is used for retrieving a plugin from a given extension class
     * and as a fallback supplier of an application context.
     */
    protected final PluginManager pluginManager;
    /**
     * Indicates if springs autowiring possibilities should be used.
     */
    protected final boolean autowire;

    public VTohruExtensionFactory(final PluginManager pluginManager) {
        this(pluginManager, AUTOWIRE_BY_DEFAULT);
    }

    public VTohruExtensionFactory(final PluginManager pluginManager, final boolean autowire) {
        this.pluginManager = pluginManager;
        this.autowire = autowire;
        if (!autowire) {
            log.warn("Autowiring is disabled although the only reason for existence of this special factory is" +
                     " supporting spring and its application context.");
        }
    }

    /**
     * Creates an instance of the given {@code extensionClass}. If {@link #autowire} is set to {@code true} this method
     * will try to use springs autowiring possibilities.
     *
     * @param extensionClass The class annotated with {@code @}{@link Extension}.
     * @param <T>            The type for that an instance should be created.
     * @return an instance of the the requested {@code extensionClass}.
     * @see #getApplicationContextBy(Class)
     */
    @Override
    public <T> T create(final Class<T> extensionClass) {
        if (!this.autowire) {
            log.warn("Create instance of '" + nameOf(extensionClass) + "' without using springs possibilities as" +
                     " autowiring is disabled.");
            return createWithoutVthoru(extensionClass);
        }

        return getApplicationContextBy(extensionClass)
            .map(applicationContext -> createWithVTohru(extensionClass, applicationContext))
            .orElseGet(() -> createWithoutVthoru(extensionClass));
    }

    protected <T> T createWithVTohru(final Class<T> extensionClass, final VerticleApplicationContext applicationContext) {

        log.debug("Instantiate extension class '" + nameOf(extensionClass) + "' by using constructor autowiring.");
        // Autowire by constructor. This does not include the other types of injection (setters and/or fields).

        T bean = applicationContext.createBean(extensionClass);
        return (T) bean;
    }

    protected <T> Optional<VerticleApplicationContext> getApplicationContextBy(final Class<T> extensionClass) {
        final Plugin plugin = Optional.ofNullable(this.pluginManager.whichPlugin(extensionClass))
            .map(PluginWrapper::getPlugin)
            .orElse(null);

        final VerticleApplicationContext applicationContext;

        if (plugin instanceof VTohruPlugin) {
            log.debug("  Extension class ' " + nameOf(extensionClass) + "' belongs to spring-plugin '" + nameOf(plugin)
                      + "' and will be autowired by using its application context.");
            applicationContext = ((VTohruPlugin) plugin).getApplicationContext();
        } else if (this.pluginManager instanceof VTohruPluginManager) {
            log.debug("  Extension class ' " + nameOf(extensionClass) + "' belongs to a non spring-plugin (or main application)" +
                      " '" + nameOf(plugin) + ", but the used PF4J plugin-manager is a spring-plugin-manager. Therefore" +
                      " the extension class will be autowired by using the managers application contexts");
            applicationContext = ((VTohruPluginManager) this.pluginManager).getApplicationContext();
        } else {
            log.warn("  No application contexts can be used for instantiating extension class '" + nameOf(extensionClass) + "'."
                     + " This extension neither belongs to a PF4J spring-plugin (id: '" + nameOf(plugin) + "') nor is the used" +
                     " plugin manager a spring-plugin-manager (used manager: '" + nameOf(this.pluginManager.getClass()) + "')." +
                     " At perspective of PF4J this seems highly uncommon in combination with a factory which only reason for existence" +
                     " is using spring (and its application context) and should at least be reviewed. In fact no autowiring can be" +
                     " applied although autowire flag was set to 'true'. Instantiating will fallback to standard Java reflection.");
            applicationContext = null;
        }

        return Optional.ofNullable(applicationContext);
    }

    /**
     * Creates an instance of the given class object by using standard Java reflection.
     *
     * @param extensionClass The class annotated with {@code @}{@link Extension}.
     * @param <T>            The type for that an instance should be created.
     * @return an instantiated extension.
     * @throws IllegalArgumentException if the given class object has no public constructor.
     * @throws RuntimeException         if the called constructor cannot be instantiated with {@code null}-parameters.
     */
    @SuppressWarnings("unchecked")
    protected <T> T createWithoutVthoru(final Class<T> extensionClass) throws IllegalArgumentException {
        final Constructor<?> constructor = getPublicConstructorWithShortestParameterList(extensionClass)
            // An extension class is required to have at least one public constructor.
            .orElseThrow(() -> new IllegalArgumentException("Extension class '" + nameOf(extensionClass)
                                                            + "' must have at least one public constructor."));
        try {
            log.debug("Instantiate '" + nameOf(extensionClass) + "' by calling '" + constructor + "'with standard Java reflection.");
            // Creating the instance by calling the constructor with null-parameters (if there are any).
            return (T) constructor.newInstance(nullParameters(constructor));
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            // If one of these exceptions is thrown it it most likely because of NPE inside the called constructor and
            // not the reflective call itself as we precisely searched for a fitting constructor.
            log.error(ex.getMessage(), ex);
            throw new RuntimeException("Most likely this exception is thrown because the called constructor (" + constructor + ")" +
                                       " cannot handle 'null' parameters. Original message was: "
                                       + ex.getMessage(), ex);
        }
    }

    private Optional<Constructor<?>> getPublicConstructorWithShortestParameterList(final Class<?> extensionClass) {
        return Stream.of(extensionClass.getConstructors())
            .min(Comparator.comparing(Constructor::getParameterCount));
    }

    private Object[] nullParameters(final Constructor<?> constructor) {
        return new Object[constructor.getParameterCount()];
    }

    private String nameOf(final Plugin plugin) {
        return nonNull(plugin)
            ? plugin.getWrapper().getPluginId()
            : "system";
    }

    private <T> String nameOf(final Class<T> clazz) {
        return clazz.getName();
    }
}
