package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.GetDeliveryVehicleEvent;
import bgu.spl.mics.application.messages.ReleaseDeliveryVehicleEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 */
public class ResourceService extends MicroService{
	private ResourcesHolder resource;
	private LinkedBlockingQueue<Future<DeliveryVehicle>> waiting=new LinkedBlockingQueue<>();

	/**
	 * Constructor
	 * @param name
	 */
	public ResourceService(String name) {
		super(name);
		resource = ResourcesHolder.getInstance();
	}

	@Override
	/**
	 * Defines Which Events and Broadcasts this service will be subscribed to.
	 */
	protected void initialize() {
		subscribeEvent((new GetDeliveryVehicleEvent().getClass()),(event)->{
			Future<DeliveryVehicle> future = resource.acquireVehicle();
			if (!future.isDone()) //If there was no vehicles available, add future to the waiting list
				waiting.add(future);
			complete((GetDeliveryVehicleEvent)event,future);
		});
		subscribeEvent((new ReleaseDeliveryVehicleEvent()).getClass(),(event)->{
			resource.releaseVehicle(event.getDeliveryVehicle());
			complete((ReleaseDeliveryVehicleEvent)event,true);
			});
		subscribeBroadcast((new FinalTickBroadcast()).getClass(), (event) -> {
			Iterator <Future<DeliveryVehicle>> iter = waiting.iterator();
			while (iter.hasNext()) // Resolve all futures that stiil waiting to be resolved to null
				iter.next().resolve(null);
			this.terminate(); // Kill This Service
		});
	}

}
