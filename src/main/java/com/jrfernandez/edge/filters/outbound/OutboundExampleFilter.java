package com.jrfernandez.edge.filters.outbound;

import com.netflix.zuul.Filter;
import com.netflix.zuul.filters.FilterSyncType;
import com.netflix.zuul.filters.FilterType;
import com.netflix.zuul.filters.http.HttpOutboundSyncFilter;
import com.netflix.zuul.message.http.HttpResponseMessage;

@Filter(order = 1, type = FilterType.OUTBOUND, sync = FilterSyncType.SYNC)
public class OutboundExampleFilter extends HttpOutboundSyncFilter {

    @Override
    public HttpResponseMessage apply(HttpResponseMessage response) {
        response.getHeaders().add("foo", "bar");
        return response;
    }

    @Override
    public boolean shouldFilter(HttpResponseMessage msg) {
        return true;
    }
}
