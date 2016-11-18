package de.js.cb;

import de.js.cb.exc.CircuitBreakerException;
import de.js.cb.exc.FailureThresholdExceeded;
import de.js.cb.req.*;

public class Main {

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        CircuitBreaker circuitBreaker = getCircuitBreaker(Request.class);

        try {
            Response response = circuitBreaker.run(Response.class);
        } catch (FailureThresholdExceeded e) {
            System.out.println("Threshold exceeded");
        } catch (CircuitBreakerException e) {
            e.printStackTrace();
        }
    }

    CircuitBreaker getCircuitBreaker(Class<? extends Callable> clazz) {
        try {
            Callable c = CallableFactory.instance(clazz);
            CircuitBreaker circuitBreaker = new CircuitBreaker(c);
            return circuitBreaker.withFailureThreshold(5);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }
}
