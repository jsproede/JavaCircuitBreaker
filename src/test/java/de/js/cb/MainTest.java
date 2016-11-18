package de.js.cb;

import de.js.cb.exc.CircuitBreakerException;
import de.js.cb.exc.CircuitBreakerThresholdExceeded;
import de.js.cb.req.CircuitBreaker;
import de.js.cb.req.Request;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainTest {

    private Main main;

    @Before
    public void setUp() throws Exception {
        main = new Main();
    }

    @Test
    public void circuitBreakerTest() throws Exception {
        CircuitBreaker circuitBreaker = getRequestCircuitBreaker();
        Response response = circuitBreaker.run(Response.class);
        check(response);
        assertTrue(circuitBreaker.isOpen());
        assertFalse(circuitBreaker.isHalfOpen());
    }

    @Test
    public void circuitBreakerWithFailure() throws Exception {
        CircuitBreaker circuitBreaker = getRequestCircuitBreaker();
        circuitBreaker.simulateFailure();

        try {
            circuitBreaker.run(Response.class);
        } catch (CircuitBreakerException e) {
            assertTrue(e instanceof CircuitBreakerThresholdExceeded);
            assertFalse(circuitBreaker.isOpen());
            assertFalse(circuitBreaker.isHalfOpen());
        }

        circuitBreaker.halfOpen();
        Response response = circuitBreaker.run(Response.class);
        check(response);
    }

    @Test
    public void circuitBreakerHalfOpened() throws Exception {
        CircuitBreaker circuitBreaker = getRequestCircuitBreaker();
        assertTrue(circuitBreaker.isHalfOpen());
        assertFalse(circuitBreaker.isOpen());
    }

    @Test(expected = CircuitBreakerThresholdExceeded.class)
    public void circuitBreakerClosed() throws Exception {
        CircuitBreaker circuitBreaker = getRequestCircuitBreaker();
        circuitBreaker.close();
        circuitBreaker.run(Response.class);
    }

    @Test(expected = CircuitBreakerThresholdExceeded.class)
    public void reuseCircuitBreakerAndSimulateFailure() throws Exception {
        CircuitBreaker circuitBreaker = getRequestCircuitBreaker();
        Response response = circuitBreaker.run(Response.class);
        check(response);

        response = circuitBreaker.run(Response.class);
        check(response);

        circuitBreaker.simulateFailure();
        circuitBreaker.run(Response.class);
    }

    private void check(Response response) {
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
    }

    private CircuitBreaker getRequestCircuitBreaker() {
        return main.getCircuitBreaker(Request.class);
    }
}