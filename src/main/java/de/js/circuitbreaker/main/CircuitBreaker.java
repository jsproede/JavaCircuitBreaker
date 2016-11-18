package de.js.circuitbreaker.main;

import de.js.circuitbreaker.exceptions.CircuitBreakerException;
import de.js.circuitbreaker.exceptions.CircuitBreakerFailureException;
import de.js.circuitbreaker.exceptions.CircuitBreakerThresholdExceeded;

public class CircuitBreaker {

    private Callable callable;

    private boolean open;
    private boolean halfOpen;

    private int failureThreshold;
    private int retries;

    private CircuitMonitor monitor;

    public CircuitBreaker(Callable callable) {
        this.callable = callable;
        this.monitor = new CircuitMonitor(this);
        halfOpen();
    }

    public CircuitBreaker withFailureThreshold(int failureThreshold) {
        this.failureThreshold = failureThreshold;
        return this;
    }

    public <T> T run(Class<T> returnType) throws CircuitBreakerException {
        callable.prepare();

        while (open || ((halfOpen && !open) && retries < failureThreshold)) {
            try {
                T o = callable.call(returnType);
                open();
                monitor.logCurrentState();
                return o;
            } catch (CircuitBreakerFailureException e) {
                retries++;
            }
        }

        monitor.logFailure();
        close();
        monitor.logCurrentState();
        throw new CircuitBreakerThresholdExceeded();
    }

    int getRetries() {
        return retries;
    }

    private void open() {
        this.open = true;
        this.halfOpen = false;
    }

    public void halfOpen() {
        this.open = false;
        this.halfOpen = true;
        this.retries = 0;
    }

    public void close() {
        this.open = false;
        this.halfOpen = false;
        this.retries = 0;
    }

    public boolean isOpen() {
        return this.open;
    }

    public boolean isHalfOpen() {
        return this.halfOpen;
    }

    public void simulateFailure() {
        halfOpen();
        retries = failureThreshold;
    }
}
