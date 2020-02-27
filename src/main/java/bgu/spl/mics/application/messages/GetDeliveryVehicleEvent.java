package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class GetDeliveryVehicleEvent implements Event <Future<DeliveryVehicle>> {
    private Future<DeliveryVehicle> deliveryVehicle;

    /**
     * Empty Constructor
     */
    public GetDeliveryVehicleEvent (){
    }

}
