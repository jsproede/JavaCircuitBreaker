package de.js.circuitbreaker;

import de.js.circuitbreaker.callables.Request;
import de.js.circuitbreaker.exceptions.CircuitBreakerException;
import de.js.circuitbreaker.exceptions.CircuitBreakerThresholdExceeded;
import de.js.circuitbreaker.main.Callable;
import de.js.circuitbreaker.main.CircuitBreaker;
import de.js.circuitbreaker.models.Response;

public class Main {

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        CircuitBreaker circuitBreaker = getCircuitBreaker(Request.class);

        try {
            Response response = circuitBreaker.run(Response.class);
        } catch (CircuitBreakerThresholdExceeded e) {
            System.out.println("Threshold exceeded");
        } catch (CircuitBreakerException e) {
            e.printStackTrace();
        }
    }

    CircuitBreaker getCircuitBreaker(Class<? extends Callable> clazz) {
        try {
            Callable c = Callable.newInstance(clazz);
            CircuitBreaker circuitBreaker = new CircuitBreaker(c);
            return circuitBreaker.withFailureThreshold(5);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }
}
