package com.example.jsonfetcher.components.adapter.jpclient.internal.resilence;

import com.example.jsonfetcher.components.integration.postfetcher.spi.fetching.PostRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@ConditionalOnProperty(value = "resilience4j.enabled", havingValue = "true", matchIfMissing = true)
class Resilence4JConfig {

    @Bean
    static OrderedBeanPostProcessor resilence4JJpPostProviderWrapper(ObjectFactory<RetryRegistry> retryRegistry,
                                                                     ObjectFactory<CircuitBreakerRegistry> circuitBreakerRegistry) {
        return new OrderedBeanPostProcessor() {

            @Override
            public int getOrder() {
                return Ordered.HIGHEST_PRECEDENCE;
            }

            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) {
                if (bean instanceof PostRepository postRepository) {
                    return new Resilence4JPostRepository(postRepository, retryRegistry.getObject(), circuitBreakerRegistry.getObject());
                }
                return bean;
            }
        };
    }


    interface OrderedBeanPostProcessor extends Ordered, BeanPostProcessor { }
}
