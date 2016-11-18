package de.js.cb.req;

class CircuitMonitor {

    private CircuitBreaker circuitBreaker;

    CircuitMonitor(CircuitBreaker circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
    }

    void logCurrentState() {
        if (circuitBreaker == null) return;
        if (circuitBreaker.isOpen()) {
            log("is open");
        } else if (circuitBreaker.isHalfOpen()) {
            log("didn't send any requests since instantiation");
        } else {
            log("is closed");
        }
    }

    private void log(String s) {
        System.out.println(circuitBreaker + " " + s);
    }
}
