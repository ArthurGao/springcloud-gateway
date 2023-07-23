package nz.paymark.proxy.datastudio.common.log;
import nz.paymark.proxy.datastudio.common.Context;

import java.util.Date;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

@Service
public class SplunkMessageLogService {

    public void execute() {
        Context context = Context.getInstance();
        String proxyPathsuffix = context.getVariable("proxy.pathsuffix");
        String verb = context.getVariable("request.verb");

        String flow = context.getVariable("flow");
        String timestamp = context.getVariable("system.time");
        String apiName = context.getVariable("apiproxy.name");
        String environment = context.getVariable("environment.name");
        String flowName = context.getVariable("current.flow.name");
        String suigeneris = context.getVariable("messageid");

        LogData logData = new LogData();
        logData.setLogsource("Apigee");
        logData.setLogpoint("");
        logData.setTimestamp(getTimeStamp());
        logData.setSuigeneris(suigeneris);
        logData.setService(apiName);
        logData.setEnvironment(environment);
        logData.setProxyPathsuffix(proxyPathsuffix);

        switch (flow) {
            case "PROXY_REQ_FLOW":
                logData.setLogpoint("REQUEST-PROXY");
                logData.setVerb((String) context.getVariable("request.verb"));

                context.setVariable("proxyStartTime", getCurrentTimeInMillis());
                break;

            case "PROXY_RESP_FLOW":
                logData.setLogpoint("PROXY-RESPONSE");
                logData.setHeaders(context.getVariable("proxyResponse.headers"));
                logData.setStatusCode((String) context.getVariable("response.status.code"));

                long proxyStartTime = (long) context.getVariable("proxyStartTime");
                long timeTaken = getCurrentTimeInMillis() - proxyStartTime;
                logData.setTimeTaken(timeTaken);
                break;

            case "TARGET_REQ_FLOW":
                logData.setLogpoint("TARGET-REQUEST");

                context.setVariable("targetStartTime", getCurrentTimeInMillis());
                break;

            case "TARGET_RESP_FLOW":
                logData.setLogpoint("TARGET-RESPONSE");
                logData.setHeaders(context.getVariable("targetResponse.headers"));
                logData.setStatusCode((String) context.getVariable("response.status.code"));
                logData.setTargetUrl(context.getVariable("target.scheme") + "://" + context.getVariable("target.host") + ":" + context.getVariable("target.port") + context.getVariable("request.uri"));

                long targetStartTime = (long) context.getVariable("targetStartTime");
                timeTaken = getCurrentTimeInMillis() - targetStartTime;
                logData.setTimeTaken(timeTaken);
                break;

            case "ERROR":
                logData.setLogpoint("ERROR");
                logData.setHeaders(context.getVariable("error.headers"));

                String httpErrorCode = context.getVariable("httpErrorCode");
                if (httpErrorCode != null) { // If caught by common exception handling framework
                    logData.setStatusCode(httpErrorCode);
                    logData.setContent(context.getVariable("errorContent"));
                    logData.setInternalError(context.getVariable("error.content"));
                } else {
                    logData.setStatusCode((String) context.getVariable("message.status.code"));
                    logData.setContent(context.getVariable("message.content"));
                }

                proxyStartTime = (long) context.getVariable("proxyStartTime");
                timeTaken = getCurrentTimeInMillis() - proxyStartTime;
                logData.setTimeTaken(timeTaken);
                break;
        }

        context.setVariable("logData", logData.toJsonString());
    }

    private String getTimeStamp() {
        return new Date().toString();
    }

    private long getCurrentTimeInMillis() {
        return new Date().getTime();
    }

    private static class LogData {
        private String logsource;
        private String logpoint;
        private String timestamp;
        private String suigeneris;
        private String service;
        private String environment;
        private String proxyPathsuffix;
        private String verb;
        private String headers;
        private String statusCode;
        private String targetUrl;
        private String content;
        private String internalError;
        private long timeTaken;

        public String getLogsource() {
            return logsource;
        }

        public void setLogsource(String logsource) {
            this.logsource = logsource;
        }

        public String getLogpoint() {
            return logpoint;
        }

        public void setLogpoint(String logpoint) {
            this.logpoint = logpoint;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getSuigeneris() {
            return suigeneris;
        }

        public void setSuigeneris(String suigeneris) {
            this.suigeneris = suigeneris;
        }

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public String getEnvironment() {
            return environment;
        }

        public void setEnvironment(String environment) {
            this.environment = environment;
        }

        public String getProxyPathsuffix() {
            return proxyPathsuffix;
        }

        public void setProxyPathsuffix(String proxyPathsuffix) {
            this.proxyPathsuffix = proxyPathsuffix;
        }

        public String getVerb() {
            return verb;
        }

        public void setVerb(String verb) {
            this.verb = verb;
        }

        public String getHeaders() {
            return headers;
        }

        public void setHeaders(String headers) {
            this.headers = headers;
        }

        public String getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(String statusCode) {
            this.statusCode = statusCode;
        }

        public String getTargetUrl() {
            return targetUrl;
        }

        public void setTargetUrl(String targetUrl) {
            this.targetUrl = targetUrl;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getInternalError() {
            return internalError;
        }

        public void setInternalError(String internalError) {
            this.internalError = internalError;
        }

        public long getTimeTaken() {
            return timeTaken;
        }

        public void setTimeTaken(long timeTaken) {
            this.timeTaken = timeTaken;
        }

        public String toJsonString() {
            // Convert logData object to JSON string representation
            // You can use a JSON library like Gson or Jackson to perform the conversion
            // Here's an example using Gson:
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }
}
