package de.js.cb.req;

import de.js.cb.exc.FailureException;

public class Request extends Callable {

    Request() {}

    @Override
    protected void prepare() {

    }

    @Override
    protected <T> T call(Class<T> returnType) throws FailureException {
        T o;
        try {
            o = returnType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new FailureException();
        }
        return o;
    }
}
