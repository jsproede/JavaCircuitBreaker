package de.js.cb.req;

class CircuitMonitor {

    private CircuitBreaker circuitBreaker;

    CircuitMonitor(CircuitBreaker circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
    }

    void logCurrentState() {
        if (circuitBreaker == null) return;
        if (circuitBreaker.isOpen()) {
            System.out.println("CircuitBreaker is open");
        } else if (circuitBreaker.isHalfOpen()) {
            System.out.println("CircuitBreaker didn't send any requests since instantiation");
        } else {
            System.out.println("CircuitBreaker is closed");
        }
    }
}
