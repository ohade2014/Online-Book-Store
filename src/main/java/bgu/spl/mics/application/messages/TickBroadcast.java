package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private int counter;

    /**
     * Constructor
     * @param Counter
     */
    public TickBroadcast(int Counter){
        this.counter=Counter;
    }

    /**
     * Empty Constructor
     */
    public TickBroadcast(){}

    /**
     *
     * @return the tick sending to Services
     */
    public int getTick(){
        return counter;
    }
}
