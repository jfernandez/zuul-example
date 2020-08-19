package com.jrfernandez.edge.filters.inbound;

import com.netflix.zuul.Filter;
import com.netflix.zuul.context.SessionContext;
import com.netflix.zuul.filters.FilterSyncType;
import com.netflix.zuul.filters.FilterType;
import com.netflix.zuul.filters.endpoint.ProxyEndpoint;
import com.netflix.zuul.filters.http.HttpInboundSyncFilter;
import com.netflix.zuul.message.http.HttpRequestMessage;

@Filter(order = 1, type = FilterType.INBOUND, sync = FilterSyncType.SYNC)
public class InboundExampleFilter extends HttpInboundSyncFilter {

    @Override
    public boolean shouldFilter(HttpRequestMessage msg) {
        return true;
    }

    @Override
    public HttpRequestMessage apply(HttpRequestMessage request) {
        SessionContext context = request.getContext();
        context.setEndpoint(ProxyEndpoint.class.getCanonicalName());
        context.setRouteVIP("api");
        return request;
    }
}
