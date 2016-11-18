package de.js.circuitbreaker.main;

import de.js.circuitbreaker.exceptions.CircuitBreakerException;
import de.js.circuitbreaker.exceptions.CircuitBreakerFailureException;
import de.js.circuitbreaker.exceptions.CircuitBreakerThresholdExceeded;

public class CircuitBreaker {

    private enum State {
        OPEN, CLOSED, HALF_OPEN
    }

    private Callable callable;

    private State state = State.HALF_OPEN;

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

        while (state == State.OPEN || (state == State.HALF_OPEN && retries < failureThreshold)) {
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
        this.state = State.OPEN;
    }

    public void halfOpen() {
        this.state = State.HALF_OPEN;
        this.retries = 0;
    }

    public void close() {
        this.state = State.CLOSED;
        this.retries = 0;
    }

    public boolean isOpen() {
        return this.state == State.OPEN;
    }

    public boolean isHalfOpen() {
        return this.state == State.HALF_OPEN;
    }

    public void simulateFailure() {
        halfOpen();
        retries = failureThreshold;
    }
}