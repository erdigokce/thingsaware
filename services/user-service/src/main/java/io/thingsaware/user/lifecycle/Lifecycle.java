package io.thingsaware.user.lifecycle;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vertx.ext.consul.Check;
import io.vertx.ext.consul.ServiceOptions;
import io.vertx.mutiny.ext.consul.ConsulClient;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
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

    public static final Duration MAX_AWAIT_DURATION = Duration.of(5, ChronoUnit.SECONDS);
    @ConfigProperty(name = "quarkus.application.name")
    String appName;
    @ConfigProperty(name = "quarkus.application.version")
    String appVersion;
    private final ConsulClient consulClient;
    private String instanceId;
    ScheduledFuture<?> scheduledServiceRegistration;

    void onStart(@Observes StartupEvent ev) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        scheduledServiceRegistration = executorService.schedule(() -> {
            List<Check> list = consulClient.healthChecks(appName)
                .onCancellation().invoke(() -> log.error("Health checks could not be retrieved for app {}", appName))
                .await().atMost(MAX_AWAIT_DURATION)
                .getList();
            instanceId = appName + "-" + list.size();
            int port = Integer.parseInt(System.getProperty("quarkus.http.port"));
            ServiceOptions serviceOptions = new ServiceOptions();
            serviceOptions.setId(instanceId);
            serviceOptions.setName(appName);
            serviceOptions.setAddress("127.0.0.1");
            serviceOptions.setPort(port);
            serviceOptions.setMeta(Collections.singletonMap("version", appVersion));
            consulClient.registerService(serviceOptions)
                .onCancellation().invoke(() -> log.error("Service {} could not be registered. ID: {} ", appName, instanceId))
                .await().atMost(MAX_AWAIT_DURATION);
            log.info("Instance registered: id={}, address=127.0.0.1:{}", instanceId, port);
        }, 5000, TimeUnit.MILLISECONDS);
    }

    void onStop(@Observes ShutdownEvent ev) {
        consulClient.deregisterService(instanceId).await().atMost(MAX_AWAIT_DURATION);
        scheduledServiceRegistration.cancel(true);
        log.info("Instance de-registered: id={}", instanceId);
    }

}
