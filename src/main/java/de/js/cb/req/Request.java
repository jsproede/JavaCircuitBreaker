package de.js.cb.req;

import de.js.cb.exc.CircuitBreakerFailureException;

public class Request extends Callable {

    Request() {}

    @Override
    protected void prepare() {

    }

    @Override
    protected <T> T call(Class<T> returnType) throws CircuitBreakerFailureException {
        T o;
        try {
            o = returnType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CircuitBreakerFailureException();
        }
        return o;
    }
}
