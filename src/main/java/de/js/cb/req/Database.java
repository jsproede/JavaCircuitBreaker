package de.js.cb.req;

import de.js.cb.exc.CircuitBreakerFailureException;

public class Database extends Callable {

    @Override
    protected void prepare() {

    }

    @Override
    protected <T> T call(Class<T> returnType) throws CircuitBreakerFailureException {
        return null;
    }
}
