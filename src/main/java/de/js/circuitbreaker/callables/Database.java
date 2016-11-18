package de.js.circuitbreaker.callables;

import de.js.circuitbreaker.exceptions.CircuitBreakerFailureException;
import de.js.circuitbreaker.main.Callable;

public class Database extends Callable {

    @Override
    protected void prepare() {

    }

    @Override
    protected <T> T call(Class<T> returnType) throws CircuitBreakerFailureException {
        try {
            // TODO: Connect to a database or something like that
            return returnType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CircuitBreakerFailureException();
        }
    }
}
