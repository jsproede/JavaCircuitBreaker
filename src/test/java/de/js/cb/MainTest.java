package de.js.cb;

import de.js.cb.req.Callable;
import de.js.cb.req.CallableFactory;
import de.js.cb.req.CircuitBreaker;
import de.js.cb.exc.CircuitBreakerException;
import de.js.cb.exc.FailureThresholdExeeceded;
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
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
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
            assertTrue(e instanceof FailureThresholdExeeceded);
            assertFalse(circuitBreaker.isOpen());
            assertFalse(circuitBreaker.isHalfOpen());
        }
    }

    @Test
    public void circuitBreakerHalfOpened() throws Exception {
        CircuitBreaker circuitBreaker = getRequestCircuitBreaker();
        assertTrue(circuitBreaker.isHalfOpen());
        assertFalse(circuitBreaker.isOpen());
    }

    @Test(expected = FailureThresholdExeeceded.class)
    public void circuitBreakerClosed() throws Exception {
        CircuitBreaker circuitBreaker = getRequestCircuitBreaker();
        circuitBreaker.close();
        circuitBreaker.run(Response.class);
    }

    @Test(expected = FailureThresholdExeeceded.class)
    public void reuseCircuitBreakerAndSimulateFailure() throws Exception {
        CircuitBreaker circuitBreaker = getRequestCircuitBreaker();
        Response response = circuitBreaker.run(Response.class);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());

        response = circuitBreaker.run(Response.class);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());

        circuitBreaker.simulateFailure();
        circuitBreaker.run(Response.class);
    }

    private CircuitBreaker getRequestCircuitBreaker() {
        return main.getCircuitBreaker(Request.class);
    }
}