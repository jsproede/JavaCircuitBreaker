package de.js.circuitbreaker;

import de.js.circuitbreaker.callables.Database;
import de.js.circuitbreaker.callables.Request;
import de.js.circuitbreaker.exceptions.CircuitBreakerException;
import de.js.circuitbreaker.exceptions.CircuitBreakerThresholdExceeded;
import de.js.circuitbreaker.main.Callable;
import de.js.circuitbreaker.main.CircuitBreaker;
import de.js.circuitbreaker.models.DatabaseResponse;
import de.js.circuitbreaker.models.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class MainTest {

    private static final Object[][] objects = { { Request.class, Response.class }, { Database.class, DatabaseResponse.class } };

    @Parameterized.Parameters(name = "{index}: CircuitBreaker({0}, {1})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(objects);
    }

    private Main main;
    private Class<? extends Callable> callableClass;
    private Class responseType;

    public MainTest(Class<? extends Callable> callableClass, Class reponseType) {
        this.callableClass = callableClass;
        this.responseType = reponseType;
    }

    @Before
    public void setUp() throws Exception {
        main = new Main();
    }

    @Test
    public void circuitBreakerTest() throws Exception {
        CircuitBreaker circuitBreaker = getCircuitBreaker();
        Object response = circuitBreaker.run(responseType);
        check(response);
        assertTrue(circuitBreaker.isOpen());
        assertFalse(circuitBreaker.isHalfOpen());
    }

    @Test(expected = CircuitBreakerThresholdExceeded.class)
    public void circuitBreakerWithFailure() throws Exception {
        CircuitBreaker circuitBreaker = getCircuitBreaker();
        circuitBreaker.simulateFailure();
        circuitBreaker.run(responseType);
    }

    @Test
    public void circuitBreakerWithFailureAndRetryAfterwards() throws Exception {
        CircuitBreaker circuitBreaker = getCircuitBreaker();
        circuitBreaker.simulateFailure();

        try {
            circuitBreaker.run(responseType);
        } catch (CircuitBreakerException e) {
            assertTrue(e instanceof CircuitBreakerThresholdExceeded);
            assertFalse(circuitBreaker.isOpen());
            assertFalse(circuitBreaker.isHalfOpen());
        }

        circuitBreaker.halfOpen();
        Object response = circuitBreaker.run(responseType);
        check(response);
    }

    @Test
    public void circuitBreakerHalfOpened() throws Exception {
        CircuitBreaker circuitBreaker = getCircuitBreaker();
        assertTrue(circuitBreaker.isHalfOpen());
        assertFalse(circuitBreaker.isOpen());
    }

    @Test(expected = CircuitBreakerThresholdExceeded.class)
    public void circuitBreakerClosed() throws Exception {
        CircuitBreaker circuitBreaker = getCircuitBreaker();
        circuitBreaker.close();
        circuitBreaker.run(responseType);
    }

    @Test(expected = CircuitBreakerThresholdExceeded.class)
    public void reuseCircuitBreakerAndSimulateFailure() throws Exception {
        CircuitBreaker circuitBreaker = getCircuitBreaker();
        Object response = circuitBreaker.run(responseType);
        check(response);

        response = circuitBreaker.run(responseType);
        check(response);

        circuitBreaker.simulateFailure();
        circuitBreaker.run(responseType);
    }

    private void check(Object o) {
        switch (o.getClass().getSimpleName()) {
            case "DatabaseResponse":
                DatabaseResponse databaseResponse = (DatabaseResponse)o;
                assertNotNull(databaseResponse);
                assertEquals(404, databaseResponse.getStatusCode());
                break;
            case "Response":
                Response response = (Response)o;
                assertNotNull(response);
                assertEquals(200, response.getStatusCode());
                break;
        }
    }

    private CircuitBreaker getCircuitBreaker() {
        return main.getCircuitBreaker(this.callableClass);
    }
}