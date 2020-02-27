package bgu.spl.mics;

import java.util.concurrent.atomic.AtomicInteger;

public class ServicesInitCounter {

    private AtomicInteger counter = new AtomicInteger();
    private static ServicesInitCounter instance = null;

    /**
     * Constructor
     */
    public ServicesInitCounter (){
        counter.set(0);
    }

    /**
     * Creates the single instance of this class
     */
    private static class SingletonServiceInitCounter {
        private static ServicesInitCounter instance = new ServicesInitCounter();
    }

    /**
     *
     * @return the single instance of this class
     */
    public static ServicesInitCounter getInstance() {
        return SingletonServiceInitCounter.instance;
    }

    /**
     * Increase the counter of Services activated by one
     */
    public void Plusplus (){
        counter.incrementAndGet();
    }

    /**
     * @return The int value of the counter counts the initialized services
     */
    public int getIntValue(){
        return counter.intValue();
    }
}
