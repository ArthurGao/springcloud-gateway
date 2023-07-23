package nz.arthur.proxy.datastudio.common;

import java.util.concurrent.ConcurrentHashMap;

public class Context {
    private static volatile Context instance;
    private ConcurrentHashMap<String, Object> variables;

    private Context() {
        // Private constructor to prevent instantiation
        variables = new ConcurrentHashMap<>();
    }

    public static Context getInstance() {
        if (instance == null) {
            synchronized (Context.class) {
                if (instance == null) {
                    instance = new Context();
                }
            }
        }
        return instance;
    }

    public <T> T getVariable(String key) {
        return (T) variables.get(key);
    }

    public <T> void setVariable(String key, T value) {
        variables.put(key, value);
    }
}
