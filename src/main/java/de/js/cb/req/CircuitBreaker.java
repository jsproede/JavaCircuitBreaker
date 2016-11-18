package de.js.cb.req;

import de.js.cb.exc.CircuitBreakerException;
import de.js.cb.exc.FailureException;
import de.js.cb.exc.FailureThresholdExceeded;

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

        while(open || ((halfOpen && !open) && retries < failureThreshold)) {
            try {
                T o = callable.call(returnType);
                open();
                monitor.logCurrentState();
                return o;
            } catch (FailureException e) {
                retries++;
            }
        }

        close();
        monitor.logCurrentState();
        throw new FailureThresholdExceeded();
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
        retries = failureThreshold + 1;
    }
}
