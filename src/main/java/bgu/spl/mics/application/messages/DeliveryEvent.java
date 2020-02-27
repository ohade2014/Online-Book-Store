package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class DeliveryEvent<Boolean> implements Event {
    private String Address;
    private int Distance;

    /**
     * Constructor
     * @param address
     * @param distance
     */
    public DeliveryEvent (String address, int distance){
        Address=address;
        Distance=distance;
    }

    /**
     * Empty Constructor
     */
    public DeliveryEvent (){
        Address="";
        Distance=0;
    }

    /**
     * Get Delivery's Address
     * @return Address destination of delivery
     */
    public String getAddress(){
        return Address;
    }

    /**
     * Get Distance of Delivery
     * @return Delivery distance
     */
    public int getDistance(){
        return Distance;
    }

}
