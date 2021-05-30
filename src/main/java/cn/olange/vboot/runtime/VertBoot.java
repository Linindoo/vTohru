package cn.olange.vboot.runtime;

import cn.olange.vboot.context.VerticleApplicationContext;
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

public class VertBoot extends DefaultApplicationContextBuilder {
    private static final String BANNER_NAME = "micronaut-banner.txt";
    private static final Logger logger = LoggerFactory.getLogger(VertBoot.class);
    private static final String SHUTDOWN_MONITOR_THREAD = "micronaut-shutdown-monitor-thread";
    private Map<Class<? extends Throwable>, Function<Throwable, Integer>> exitHandlers = new LinkedHashMap();

    @NonNull
    public ApplicationContext start() {
        long start = System.currentTimeMillis();
        this.printBanner();
        ApplicationContext applicationContext = super.build();
        try {
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
    public VertBoot include(@Nullable String... configurations) {
        return (VertBoot)super.include(configurations);
    }

    @NonNull
    public VertBoot exclude(@Nullable String... configurations) {
        return (VertBoot)super.exclude(configurations);
    }

    @NonNull
    public VertBoot banner(boolean isEnabled) {
        return (VertBoot)super.banner(isEnabled);
    }

    @NonNull
    public VertBoot classes(@Nullable Class... classes) {
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
    public VertBoot properties(@Nullable Map<String, Object> properties) {
        return (VertBoot)super.properties(properties);
    }

    @NonNull
    public VertBoot singletons(Object... beans) {
        return (VertBoot)super.singletons(beans);
    }

    @NonNull
    public VertBoot propertySources(@Nullable PropertySource... propertySources) {
        return (VertBoot)super.propertySources(propertySources);
    }

    @NonNull
    public VertBoot environmentPropertySource(boolean environmentPropertySource) {
        return (VertBoot)super.environmentPropertySource(environmentPropertySource);
    }

    @NonNull
    public VertBoot environmentVariableIncludes(@Nullable String... environmentVariables) {
        return (VertBoot)super.environmentVariableIncludes(environmentVariables);
    }

    @NonNull
    public VertBoot environmentVariableExcludes(@Nullable String... environmentVariables) {
        return (VertBoot)super.environmentVariableExcludes(environmentVariables);
    }

    @NonNull
    public VertBoot mainClass(Class mainClass) {
        return (VertBoot)super.mainClass(mainClass);
    }

    @NonNull
    public VertBoot classLoader(ClassLoader classLoader) {
        return (VertBoot)super.classLoader(classLoader);
    }

    @NonNull
    public VertBoot args(@Nullable String... args) {
        return (VertBoot)super.args(args);
    }

    @NonNull
    public VertBoot environments(@Nullable String... environments) {
        return (VertBoot)super.environments(environments);
    }

    @NonNull
    public VertBoot defaultEnvironments(@Nullable String... environments) {
        return (VertBoot)super.defaultEnvironments(environments);
    }

    @NonNull
    public VertBoot packages(@Nullable String... packages) {
        return (VertBoot)super.packages(packages);
    }

    public static VertBoot build(String... args) {
        return (new VertBoot()).args(args);
    }

    public static ApplicationContext run(String... args) {
        return run(new Class[0], args);
    }

    public static ApplicationContext run(Class cls, String... args) {
        return run(new Class[]{cls}, args);
    }

    public static ApplicationContext run(Class[] classes, String... args) {
        return (new VertBoot()).classes(classes).args(args).start();
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

        throw new ApplicationStartupException("Error starting VertBoot server: " + exception.getMessage(), exception);
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
