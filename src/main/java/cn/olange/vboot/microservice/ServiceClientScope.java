/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.olange.vboot.microservice;

import io.micronaut.context.BeanContext;
import io.micronaut.context.BeanResolutionContext;
import io.micronaut.context.LifeCycle;
import io.micronaut.context.exceptions.DependencyInjectionException;
import io.micronaut.context.scope.CustomScope;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.type.Argument;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.BeanIdentifier;
import io.micronaut.inject.ParametrizedProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class ServiceClientScope implements CustomScope<Client>, LifeCycle<ServiceClientScope> {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceClientScope.class);
    private final BeanContext beanContext;

    public ServiceClientScope(
            BeanContext beanContext) {
        this.beanContext = beanContext;
    }

    @Override
    public boolean isRunning() {
        return true;
    }

    @Override
    public Class<Client> annotationType() {
        return Client.class;
    }

    @Override
    public <T> T get(BeanResolutionContext resolutionContext, BeanDefinition<T> beanDefinition, BeanIdentifier identifier, Provider<T> provider) {
        BeanResolutionContext.Segment segment = resolutionContext.getPath().currentSegment().orElseThrow(() ->
                new IllegalStateException("@KafkaClient used in invalid location")
        );
        Argument argument = segment.getArgument();
        Optional<AnnotationValue<Client>> annotation = argument.findAnnotation(Client.class);
        if (!(provider instanceof ParametrizedProvider)) {
            throw new DependencyInjectionException(resolutionContext, argument, "KafkaClientScope called with invalid bean provider");
        }
        return beanContext.createBean(beanDefinition.getBeanType());
    }



    @Override
    public ServiceClientScope stop() {
        return this;
    }

    @Override
    public <T> Optional<T> remove(BeanIdentifier identifier) {
        return Optional.empty();
    }

}
