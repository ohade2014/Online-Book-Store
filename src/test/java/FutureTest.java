import bgu.spl.mics.Future;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest <T> {

        private Future<String> future;
    @Before
    public void setUp() throws Exception {
         future =new Future<String>();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void get() {
        future.resolve("Success");
        String temp="Success";
        assertEquals(future.get(),temp);
    }

    @Test
    public void resolve() {
        future.resolve("start");
        assertTrue(future.get()!="Success");
        future.resolve("Success");
        assertTrue(future.get()=="Success");
    }

    @Test
    public void isDone() {
        assertFalse(future.isDone());
        future.resolve("Success");
        assertTrue(future.isDone());
    }

    @Test
    public void get1() {
        TimeUnit unit=TimeUnit.MILLISECONDS;
        long timeout=unit.convert(8,unit);
        assertNull(future.get(timeout,unit));
        future.resolve("Success");
        assertEquals(future.get(timeout,unit),"Success");
    }
}