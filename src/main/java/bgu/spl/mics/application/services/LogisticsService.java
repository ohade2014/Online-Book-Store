package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.GetDeliveryVehicleEvent;
import bgu.spl.mics.application.messages.ReleaseDeliveryVehicleEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 */
public class LogisticsService extends MicroService {

	/**
	 * Constructor
	 * @param name
	 */
	public LogisticsService(String name) {
		super(name);
	}

	@Override
	/**
	 * Defines Which Events and Broadcasts this service will be subscribed to.
	 */
	protected void initialize() {
		subscribeEvent((new DeliveryEvent()).getClass(),(event)->{
			Future<Future<DeliveryVehicle>> vehicle = sendEvent(new GetDeliveryVehicleEvent());
			if (vehicle == null){ //If Delivery did not succeeded
				complete((DeliveryEvent)event,true);}
			else {
				Future<DeliveryVehicle> vehicle_future = vehicle.get(); //get the inner future of vehicle
				if (vehicle_future == null) //If Delivery did not succeeded
					complete((DeliveryEvent) event, true);
				else {
					DeliveryVehicle vehicle_inner_future = vehicle_future.get(); //Get the vehicle resolved to this future
					if (vehicle_inner_future != null) {
						vehicle_inner_future.deliver(event.getAddress(), event.getDistance());
						Future<Boolean> future = sendEvent(new ReleaseDeliveryVehicleEvent(vehicle_inner_future));
					}
					complete((DeliveryEvent) event, true);
				}
			}
		});
		subscribeBroadcast((new FinalTickBroadcast()).getClass(), (event) -> {
			this.terminate(); //Kill this Service
		});
	}
}
