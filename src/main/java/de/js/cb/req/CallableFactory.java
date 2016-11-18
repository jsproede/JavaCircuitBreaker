package de.js.cb.req;

import java.lang.reflect.Constructor;

public class CallableFactory {

    public static Callable instance(Class<? extends Callable> callable) throws IllegalAccessException, InstantiationException {
        return callable.newInstance();
    }
}
