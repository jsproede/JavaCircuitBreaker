package de.js.cb.req;

public class CallableFactory {

    public static Callable instance(Class<? extends Callable> callable) throws IllegalAccessException, InstantiationException {
        return callable.newInstance();
    }
}
