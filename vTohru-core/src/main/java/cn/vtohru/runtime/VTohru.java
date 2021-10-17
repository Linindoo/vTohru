package cn.vtohru.runtime;

import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.util.VTohruUtil;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.DefaultApplicationContextBuilder;
import io.micronaut.context.banner.MicronautBanner;
import io.micronaut.context.banner.ResourceBanner;
import io.micronaut.context.env.Environment;
import io.micronaut.context.env.PropertySource;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.naming.Described;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.runtime.exceptions.ApplicationStartupException;
import io.micronaut.runtime.server.EmbeddedServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

public class VTohru extends DefaultApplicationContextBuilder {
    private static final String BANNER_NAME = "micronaut-banner.txt";
    private static final Logger logger = LoggerFactory.getLogger(VTohru.class);
    private static final String SHUTDOWN_MONITOR_THREAD = "micronaut-shutdown-monitor-thread";
    private Map<Class<? extends Throwable>, Function<Throwable, Integer>> exitHandlers = new LinkedHashMap();

    @NonNull
    public VerticleApplicationContext start() {
        long start = System.currentTimeMillis();
        this.printBanner();
        VerticleApplicationContext applicationContext = (VerticleApplicationContext) super.build();
        try {
            VTohruUtil.setContext(applicationContext);
            applicationContext.registerSingleton(applicationContext, false);
            applicationContext.start();
            Optional<EmbeddedApplication> embeddedContainerBean = applicationContext.findBean(EmbeddedApplication.class);
            embeddedContainerBean.ifPresent((embeddedApplication) -> {
                try {
                    embeddedApplication.start();
                    boolean keepAlive = false;
                    long end;
                    long took;
                    if (embeddedApplication instanceof Described) {
                        if (logger.isInfoEnabled()) {
                            end = System.currentTimeMillis();
                            took = end - start;
                            String desc = ((Described)embeddedApplication).getDescription();
                            logger.info("Startup completed in {}ms. Server Running: {}", took, desc);
                        }

                        keepAlive = embeddedApplication.isServer();
                    } else if (embeddedApplication instanceof EmbeddedServer) {
                        EmbeddedServer embeddedServer = (EmbeddedServer)embeddedApplication;
                        if (logger.isInfoEnabled()) {
                            long endx = System.currentTimeMillis();
                            long tookx = endx - start;
                            URL url = embeddedServer.getURL();
                            logger.info("Startup completed in {}ms. Server Running: {}", tookx, url);
                        }

                        keepAlive = embeddedServer.isKeepAlive();
                    } else {
                        if (logger.isInfoEnabled()) {
                            end = System.currentTimeMillis();
                            took = end - start;
                            logger.info("Startup completed in {}ms.", took);
                        }

                        keepAlive = embeddedApplication.isServer();
                    }

                    Thread mainThread = Thread.currentThread();
                    CountDownLatch countDownLatch = new CountDownLatch(1);
                    boolean finalKeepAlive = keepAlive;
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        if (logger.isInfoEnabled()) {
                            logger.info("Embedded Application shutting down");
                        }

                        if (embeddedApplication.isRunning()) {
                            embeddedApplication.stop();
                            countDownLatch.countDown();
                            if (finalKeepAlive) {
                                mainThread.interrupt();
                            }
                        }

                    }));
                    if (keepAlive) {
                        (new Thread(() -> {
                            try {
                                if (!embeddedApplication.isRunning()) {
                                    countDownLatch.countDown();
                                    Thread.sleep(1000L);
                                }
                            } catch (InterruptedException var3) {
                            }

                        }, "micronaut-shutdown-monitor-thread")).start();

                        try {
                            countDownLatch.await();
                        } catch (InterruptedException var12) {
                        }

                        if (logger.isInfoEnabled()) {
                            logger.info("Embedded Application shutting down");
                        }
                    }

                    if (embeddedApplication.isForceExit()) {
                        System.exit(0);
                    }
                } catch (Throwable var13) {
                    var13.printStackTrace();
                    logger.error(var13.getMessage());
                    this.handleStartupException(applicationContext.getEnvironment(), var13);
                }

            });
            if (logger.isInfoEnabled() && !embeddedContainerBean.isPresent()) {
                logger.info("No embedded container found. Running as CLI application");
            }
            return applicationContext;
        } catch (Throwable var5) {
            this.handleStartupException(applicationContext.getEnvironment(), var5);
            return null;
        }
    }

    @NonNull
    public VTohru include(@Nullable String... configurations) {
        return (VTohru)super.include(configurations);
    }

    @NonNull
    public VTohru exclude(@Nullable String... configurations) {
        return (VTohru)super.exclude(configurations);
    }

    @NonNull
    public VTohru banner(boolean isEnabled) {
        return (VTohru)super.banner(isEnabled);
    }

    @NonNull
    public VTohru classes(@Nullable Class... classes) {
        if (classes != null) {
            Class[] var2 = classes;
            int var3 = classes.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Class aClass = var2[var4];
                this.packages(aClass.getPackage().getName());
            }
        }

        return this;
    }

    @NonNull
    public VTohru properties(@Nullable Map<String, Object> properties) {
        return (VTohru)super.properties(properties);
    }

    @NonNull
    public VTohru singletons(Object... beans) {
        return (VTohru)super.singletons(beans);
    }

    @NonNull
    public VTohru propertySources(@Nullable PropertySource... propertySources) {
        return (VTohru)super.propertySources(propertySources);
    }

    @NonNull
    public VTohru environmentPropertySource(boolean environmentPropertySource) {
        return (VTohru)super.environmentPropertySource(environmentPropertySource);
    }

    @NonNull
    public VTohru environmentVariableIncludes(@Nullable String... environmentVariables) {
        return (VTohru)super.environmentVariableIncludes(environmentVariables);
    }

    @NonNull
    public VTohru environmentVariableExcludes(@Nullable String... environmentVariables) {
        return (VTohru)super.environmentVariableExcludes(environmentVariables);
    }

    @NonNull
    public VTohru mainClass(Class mainClass) {
        return (VTohru)super.mainClass(mainClass);
    }

    @NonNull
    public VTohru classLoader(ClassLoader classLoader) {
        return (VTohru)super.classLoader(classLoader);
    }

    @NonNull
    public VTohru args(@Nullable String... args) {
        return (VTohru)super.args(args);
    }

    @NonNull
    public VTohru environments(@Nullable String... environments) {
        return (VTohru)super.environments(environments);
    }

    @NonNull
    public VTohru defaultEnvironments(@Nullable String... environments) {
        return (VTohru)super.defaultEnvironments(environments);
    }

    @NonNull
    public VTohru packages(@Nullable String... packages) {
        return (VTohru)super.packages(packages);
    }

    public static VTohru build(String... args) {
        return (new VTohru()).args(args);
    }

    public static VerticleApplicationContext run(String... args) {
        return run(new Class[0], args);
    }

    public static ApplicationContext run(Class cls, String... args) {
        return run(new Class[]{cls}, args);
    }

    public static VerticleApplicationContext run(Class[] classes, String... args) {
        return (new VTohru()).classes(classes).args(args).start();
    }

    protected void handleStartupException(Environment environment, Throwable exception) {
        Function<Throwable, Integer> exitCodeMapper = this.exitHandlers.computeIfAbsent(exception.getClass(), (exceptionType) -> (throwable) -> 1);
        Integer code = exitCodeMapper.apply(exception);
        if (code > 0 && !environment.getActiveNames().contains("test")) {
            if (logger.isErrorEnabled()) {
                logger.error("Error starting VertBoot server: " + exception.getMessage(), exception);
            }

            System.exit(code);
        }

        throw new ApplicationStartupException("Error starting VTohru: " + exception.getMessage(), exception);
    }

    private void printBanner() {
        if (this.isBannerEnabled()) {
            PrintStream out = System.out;
            Optional<URL> resource = this.getResourceLoader().getResource("micronaut-banner.txt");
            if (resource.isPresent()) {
                (new ResourceBanner((URL)resource.get(), out)).print();
            } else {
                (new MicronautBanner(out)).print();
            }

        }
    }

    @NonNull
    protected VerticleApplicationContext newApplicationContext() {
        return new VerticleApplicationContext(this);
    }

    @Override
    public @NonNull VerticleApplicationContext build() {
        ApplicationContext applicationContext = super.build();
        return (VerticleApplicationContext) applicationContext;
    }

}
