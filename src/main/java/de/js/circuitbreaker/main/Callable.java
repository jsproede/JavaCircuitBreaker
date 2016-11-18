package de.js.circuitbreaker.main;

import de.js.circuitbreaker.exceptions.CircuitBreakerFailureException;

public abstract class Callable {

    public static Callable newInstance(Class<? extends Callable> callable) throws IllegalAccessException, InstantiationException {
        return callable.newInstance();
    }

    protected Callable() {}

    /**
     * Override this method if you want to prepare anything in your class
     */
    protected abstract void prepare();

    /**
     * This method will be called by {code}CircuitBreaker{code}
     * Throw a CircuitBreakerFailureException if an error occured. The CircuitBreaker will retry with given
     * FailureThreshold
     *
     * @param returnType - Specifies return type
     * @return Returns instance of specified return type if no error occured or ...
     * @throws CircuitBreakerFailureException - Will be thrown when an error occured. Exception is going to be
     *                                        catched in CircuitBreaker
     */
    protected abstract <T> T call(Class<T> returnType) throws CircuitBreakerFailureException;
}
