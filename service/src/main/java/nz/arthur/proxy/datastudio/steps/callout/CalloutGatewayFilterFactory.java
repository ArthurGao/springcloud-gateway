package nz.arthur.proxy.datastudio.steps.callout;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;


import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class CalloutGatewayFilterFactory extends AbstractGatewayFilterFactory<CalloutGatewayFilterFactory.Config> {

    final Logger logger = LoggerFactory.getLogger(CalloutGatewayFilterFactory.class);


    public CalloutGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            WebClient client = WebClient.builder().build();
            return client.get()
                    .uri(config.getTargetEndpoint())
                    .exchangeToMono(clientResponse -> {
                        return (clientResponse.statusCode()
                                .is2xxSuccessful()) ? clientResponse.bodyToMono(String.class) : Mono.just(config.getVerb());
                    })
                    .map(body -> {    //response from the target
                        exchange.getRequest()
                                .mutate()
                                .headers(h -> h.set("hello",body));

                        String values = exchange.getRequest()
                                .getHeaders()
                                .get("hello")
                                .stream()
                                .collect(Collectors.joining(","));

                        logger.info(String.format("%s new header hello: value %s", exchange.getRequest().getURI(),values));

                        return exchange;
                    })
                    .flatMap(chain::filter);

        };
    }

    public static class Config {
        private String targetEndpoint;
        private String verb;

        public Config() {
        }

        public String getTargetEndpoint() {
            return targetEndpoint;
        }

        public void setTargetEndpoint(String targetEndpoint) {
            this.targetEndpoint = targetEndpoint;
        }

        public String getVerb() {
            return verb;
        }

        public void setVerb(String verb) {
            this.verb = verb;
        }
    }
}
