package de.js.cb.req;

import de.js.cb.exc.FailureException;

public abstract class Callable {

    /**
     * Override this method if you want to prepare anything in your class
     */
    protected abstract void prepare();

    /**
     * This method will be called by {code}CircuitBreaker{code}
     * Throw a FailureException if an error occured. The CircuitBreaker will retry with given
     * FailureThreshold
     * @param returnType - Specifies return type
     * @return Returns instance of specified return type if no error occured or ...
     * @throws FailureException - Will be thrown when an error occured. Exception is going to be
     * catched in CircuitBreaker
     */
    protected abstract <T> T call(Class<T> returnType) throws FailureException;
}
