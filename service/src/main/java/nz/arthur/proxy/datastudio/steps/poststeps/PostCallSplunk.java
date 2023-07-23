package nz.arthur.proxy.datastudio.steps.poststeps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import nz.arthur.proxy.datastudio.common.Context;
import nz.arthur.proxy.datastudio.steps.presteps.CallSplunk;

@Component
public class PostCallSplunk {

    final Logger logger = LoggerFactory.getLogger(PostCallSplunk.class);

    public void execute() {
        logger.info("Post Call Splunk");
        String statusCode = Context.getInstance().getVariable("ResponseStatusCode");
        logger.info("After call endpoint get status: " + statusCode);
        // splunkService.sendEventToSplunk(1, "", Context.getInstance().getVariable("logData"));
    }
}
