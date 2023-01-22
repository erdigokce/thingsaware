package io.thingsaware.service.health;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vertx.ext.consul.CheckOptions;
import io.vertx.ext.consul.CheckStatus;
import io.vertx.ext.consul.ServiceOptions;
import io.vertx.mutiny.ext.consul.ConsulClient;
import java.time.Duration;
import java.util.Collections;
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
    @ConfigProperty(name = "quarkus.http.host")
    String host;

    @ConfigProperty(name = "consul.max-await-seconds.health-checks")
    String healthChecksMaxAwaitDuration;
    @ConfigProperty(name = "consul.max-await-seconds.register-service")
    int registerServiceMaxAwaitSeconds;
    @ConfigProperty(name = "consul.max-await-seconds.deregister-service")
    int deregisterServiceMaxAwaitSeconds;
    private String instanceId;
    private ScheduledFuture<?> scheduledServiceRegistration;

    void onStart(@Observes StartupEvent ev) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        scheduledServiceRegistration = executorService.schedule(() -> {
            instanceId = appName + "-" + consulClient.healthServiceNodes(appName, false).await().atMost(Duration.ofSeconds(registerServiceMaxAwaitSeconds)).getList().size();
            int port = Integer.parseInt(System.getProperty("quarkus.http.port"));
            consulClient.registerService(createServiceOptions(port))
                .onCancellation().invoke(() -> log.error("Service {} could not be registered. ID: {} ", appName, instanceId))
                .await().atMost(Duration.ofSeconds(registerServiceMaxAwaitSeconds));
            log.info("Instance registered: id={}, address={}:{}", instanceId, host, port);
        }, 5000, TimeUnit.MILLISECONDS);
    }

    void onStop(@Observes ShutdownEvent ev) {
        scheduledServiceRegistration.cancel(true);
        consulClient.deregisterService(instanceId).await().atMost(Duration.ofSeconds(deregisterServiceMaxAwaitSeconds));
        log.info("Instance de-registered: id={}", instanceId);
    }

    private ServiceOptions createServiceOptions(int port) {
        ServiceOptions serviceOptions = new ServiceOptions();
        serviceOptions.setId(instanceId);
        serviceOptions.setName(appName);
        serviceOptions.setAddress(host);
        serviceOptions.setPort(port);
        serviceOptions.setMeta(Collections.singletonMap("version", version));
        serviceOptions.setCheckOptions(createCheckOptions(serviceOptions));
        return serviceOptions;
    }

    private CheckOptions createCheckOptions(ServiceOptions serviceOptions) {
        CheckOptions checkOptions = new CheckOptions();
        checkOptions.setId(serviceOptions.getId() + "-tcp-healthcheck");
        checkOptions.setName("TCP Health Check");
        checkOptions.setTcp(host + ":"+ serviceOptions.getPort());
        checkOptions.setInterval(healthChecksMaxAwaitDuration);
        checkOptions.setStatus(CheckStatus.WARNING);
        return checkOptions;
    }

}
