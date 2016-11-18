package de.js.cb.req;

import de.js.cb.exc.FailureException;

public abstract class Callable {

    protected abstract void prepare();
    protected abstract <T> T call(Class<T> returnType) throws FailureException;
}
