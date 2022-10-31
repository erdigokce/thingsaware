package io.thingsaware.service.health;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vertx.ext.consul.Check;
import io.vertx.ext.consul.ServiceOptions;
import io.vertx.mutiny.ext.consul.ConsulClient;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class Lifecycle {

    final ConsulClient consulClient;

    @ConfigProperty(name = "quarkus.application.name")
    String appName;
    @ConfigProperty(name = "quarkus.application.version")
    String version;
    @ConfigProperty(name = "consul.max-await-seconds.health-checks")
    int healthChecksMaxAwaitSeconds;
    @ConfigProperty(name = "consul.max-await-seconds.register-service")
    int registerServiceMaxAwaitSeconds;
    @ConfigProperty(name = "consul.max-await-seconds.deregister-service")
    int deregisterServiceMaxAwaitSeconds;
    private String instanceId;
    private ScheduledFuture<?> scheduledServiceRegistration;

    void onStart(@Observes StartupEvent ev) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        scheduledServiceRegistration = executorService.schedule(() -> {
            instanceId = appName + "-" + consulClient.localServicesAndAwait().size();
            int port = Integer.parseInt(System.getProperty("quarkus.http.port"));
            ServiceOptions serviceOptions = new ServiceOptions();
            serviceOptions.setId(instanceId);
            serviceOptions.setName(appName);
            serviceOptions.setAddress("127.0.0.1");
            serviceOptions.setPort(port);
            serviceOptions.setMeta(Collections.singletonMap("version", version));
            consulClient.registerService(serviceOptions)
                .onCancellation().invoke(() -> log.error("Service {} could not be registered. ID: {} ", appName, instanceId))
                .await().atMost(Duration.ofSeconds(registerServiceMaxAwaitSeconds));
            log.info("Instance registered: id={}, address=127.0.0.1:{}", instanceId, port);
        }, 5000, TimeUnit.MILLISECONDS);
    }

    void onStop(@Observes ShutdownEvent ev) {
        scheduledServiceRegistration.cancel(true);
        consulClient.deregisterService(instanceId).await().atMost(Duration.ofSeconds(deregisterServiceMaxAwaitSeconds));
        log.info("Instance de-registered: id={}", instanceId);
    }

}
