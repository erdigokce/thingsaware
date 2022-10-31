 package io.thingsaware.service.producer;

import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.consul.ConsulClient;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

@Singleton
public class DiscoveryProducer {

    @Singleton
    @Produces
    ConsulClient consulClient(Vertx vertx) {
        return ConsulClient.create(vertx, new ConsulClientOptions());
    }
}
