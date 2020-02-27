package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseDeliveryVehicleEvent <Boolean> implements Event {
    private DeliveryVehicle vehicle;

    /**
     * Constructor
     * @param vehicle
     */
    public ReleaseDeliveryVehicleEvent(DeliveryVehicle vehicle){
        this.vehicle = vehicle;
    }

    /**
     * Empty Constructor
     */
    public ReleaseDeliveryVehicleEvent(){
        this.vehicle = null;
    }

    /**
     * @return Vehicle made the delivery
     */
    public DeliveryVehicle getDeliveryVehicle(){
        return vehicle;
    }
}
