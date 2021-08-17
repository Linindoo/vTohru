package cn.vtohru.context;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.convert.ConversionService;
import io.micronaut.core.util.StringUtils;
import io.micronaut.core.value.PropertyResolver;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class VerticleConfigResolver extends JsonObject implements PropertyResolver {
    private final ConversionService<?> conversionService;

    public VerticleConfigResolver() {
        this.conversionService = ConversionService.SHARED;

    }

    @Override
    public boolean containsProperty(String name) {
        return this.containsKey(name);
    }

    @Override
    public boolean containsProperties(String name) {
        return this.getMap().keySet().stream().anyMatch((k) -> k.startsWith(name));
    }

    @Override
    public <T> Optional<T> getProperty(String name, ArgumentConversionContext<T> conversionContext) {
        Object value = this.getValue(name);
        return this.conversionService.convert(value, conversionContext);
    }

    @NonNull
    public Collection<String> getPropertyEntries(@NonNull String name) {
        if (StringUtils.isNotEmpty(name)) {
            String prefix = name + ".";
            return this.getMap().keySet().stream().filter((k) -> k.startsWith(prefix)).map((k) -> {
                String withoutPrefix = k.substring(prefix.length());
                int i = withoutPrefix.indexOf(46);
                return i > -1 ? withoutPrefix.substring(0, i) : withoutPrefix;
            }).collect(Collectors.toList());
        } else {
            return Collections.emptySet();
        }
    }
}
