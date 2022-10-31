package io.thingsaware.user.producer;

import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.consul.ConsulClient;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class UserServiceProducer {

    @ConfigProperty(name = "consul.host")
    String consulHost;
    @ConfigProperty(name = "consul.port")
    int consulPort;

    @Produces
    ConsulClient consulClient(Vertx vertx) {
        return ConsulClient.create(vertx, new ConsulClientOptions().setHost(consulHost).setPort(consulPort));
    }
}
