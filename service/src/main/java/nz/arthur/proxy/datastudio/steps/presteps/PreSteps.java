package nz.arthur.proxy.datastudio.steps.presteps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import nz.arthur.proxy.datastudio.steps.*;
import nz.arthur.proxy.datastudio.steps.common.FilterBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties("custom.filter.factory")
public class PreSteps extends AbstractGatewayFilterFactory<PreSteps.Config> {

    private static final Map<String, FilterBase> filterMap = new HashMap<>();

    @Autowired
    public PreSteps(CallSplunk callSplunk, SetAuthToHeader setAuthToHeader,
                    SetContext setContext, CreateLogMessage createLogMessage,
                    RequestHeaderValidation requestHeaderValidation,
                    PathAndTokenValidation pathAndTokenValidation,
                    CallOutEndpoint callOutEndpoint) {
        super(Config.class);
        filterMap.put("CallSplunk", callSplunk);
        filterMap.put("SetAuthToHeader", setAuthToHeader);
        filterMap.put("SetContext", setContext);
        filterMap.put("CreateLogMessage", createLogMessage);
        filterMap.put("CallOutEndpoint", callOutEndpoint);
        filterMap.put("RequestHeaderValidation", requestHeaderValidation);
        filterMap.put("PathAndTokenValidation", pathAndTokenValidation);
    }

    @Override
    public GatewayFilter apply(Config config) {
        List<String> names = config.getName();
        List<GatewayFilter> filters = new ArrayList<>();

        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            FilterBase filter = filterMap.get(name);
            filter.setOrder(i);
            filters.add(filter);
        }

        // Create a composite filter that contains all the configured filters
        return new PreCompositeGatewayFilter(filters);
    }

    public static class Config {
        private List<String> name;

        public List<String> getName() {
            return name;
        }

        public void setName(List<String> name) {
            this.name = name;
        }
    }


}
