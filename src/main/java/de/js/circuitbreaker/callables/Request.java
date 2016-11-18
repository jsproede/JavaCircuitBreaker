package de.js.circuitbreaker.callables;

import de.js.circuitbreaker.exceptions.CircuitBreakerFailureException;
import de.js.circuitbreaker.main.Callable;

public class Request extends Callable {

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
