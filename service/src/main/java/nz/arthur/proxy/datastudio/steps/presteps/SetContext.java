package nz.paymark.proxy.datastudio.steps.presteps;

import nz.paymark.proxy.datastudio.common.Context;
import nz.paymark.proxy.datastudio.steps.common.FilterBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class SetContext extends FilterBase {

    final Logger logger = LoggerFactory.getLogger(SetContext.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("SetContext");
        Context context = Context.getInstance();

        String proxyPathSuffix = "/qaproxies.dev.paymark.nz";
        String verb = "GET";
        String flow = "New product registration flow";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String apiName = "New-ProductRegistration";
        String environment = "TestCTEEnvironment";
        String flowName = "New product registration";
        String suiGeneris = UUID.randomUUID().toString();

        context.setVariable("proxy.pathsuffix", proxyPathSuffix);
        context.setVariable("request.verb", verb);
        context.setVariable("flow", flow);
        context.setVariable("system.time", timestamp);
        context.setVariable("apiproxy.name", apiName);
        context.setVariable("environment.name", environment);
        context.setVariable("current.flow.name", flowName);
        context.setVariable("messageid", suiGeneris);

        context.setVariable("apigee.apiproduct.name", "New-Registration");
        return chain.filter(exchange);

    }
}
