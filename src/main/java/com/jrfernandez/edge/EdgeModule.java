package com.jrfernandez.edge;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.jrfernandez.edge.filters.inbound.InboundExampleFilter;
import com.jrfernandez.edge.filters.outbound.OutboundExampleFilter;
import com.netflix.config.ConfigurationManager;
import com.netflix.discovery.guice.EurekaModule;
import com.netflix.netty.common.accesslog.AccessLogPublisher;
import com.netflix.netty.common.status.ServerStatusManager;
import com.netflix.spectator.api.DefaultRegistry;
import com.netflix.spectator.api.Registry;
import com.netflix.zuul.*;
import com.netflix.zuul.context.SessionContextDecorator;
import com.netflix.zuul.context.ZuulSessionContextDecorator;
import com.netflix.zuul.filters.ZuulFilter;
import com.netflix.zuul.filters.endpoint.ProxyEndpoint;
import com.netflix.zuul.netty.server.BaseServerStartup;
import com.netflix.zuul.netty.server.ClientRequestReceiver;
import com.netflix.zuul.origins.BasicNettyOriginManager;
import com.netflix.zuul.origins.OriginManager;
import com.netflix.zuul.stats.BasicRequestMetricsPublisher;
import com.netflix.zuul.stats.RequestMetricsPublisher;

import java.util.*;

public class EdgeModule extends AbstractModule {
    @Override
    protected void configure() {
        try {
            ConfigurationManager.loadCascadedPropertiesFromResources("application");
        } catch (Exception ex) {
            throw new RuntimeException("Error loading configuration: " + ex.getMessage(), ex);
        }

        bind(FilterLoader.class).to(StaticFilterLoader.class);
        bind(FilterFactory.class).to(DefaultFilterFactory.class);
        bind(FilterUsageNotifier.class).to(BasicFilterUsageNotifier.class);

        install(new EurekaModule());

        // sample specific bindings
        bind(BaseServerStartup.class).to(EdgeServerStartup.class);

        // use provided basic netty origin manager
        bind(OriginManager.class).to(BasicNettyOriginManager.class);

        // general server bindings
        bind(ServerStatusManager.class); // health/discovery status
        bind(SessionContextDecorator.class).to(ZuulSessionContextDecorator.class); // decorate new sessions when requests come in
        bind(Registry.class).to(DefaultRegistry.class); // atlas metrics registry
        bind(RequestCompleteHandler.class).to(BasicRequestCompleteHandler.class); // metrics post-request completion
        bind(RequestMetricsPublisher.class).to(BasicRequestMetricsPublisher.class); // timings publisher

        // access logger, including request ID generator
        bind(AccessLogPublisher.class).toInstance(new AccessLogPublisher("ACCESS",
                (channel, httpRequest) -> ClientRequestReceiver.getRequestFromChannel(channel).getContext().getUUID()));
    }

    @Provides
    @Singleton
    private Set<? extends Class<? extends ZuulFilter<?, ?>>> filterTypes() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
                InboundExampleFilter.class,
                OutboundExampleFilter.class
        )));
    }
}
