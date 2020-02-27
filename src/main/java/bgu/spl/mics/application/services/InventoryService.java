package bgu.spl.mics.application.services;

import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailability;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 */

public class InventoryService extends MicroService{
	private Inventory inventory;

	/**
	 * Constructor
	 * @param name
	 */
	public InventoryService(String name) {
		super(name);
		inventory = Inventory.getInstance();
	}

	@Override
	/**
	 * Defines Which Events and Broadcasts this service will be subscribed to.
	 */
	protected void initialize() {
		subscribeEvent((new CheckAvailability()).getClass(),(event)->{
			Integer price = inventory.checkAvailabiltyAndGetPrice(event.getBook()); //Check if book available and get its price
			this.complete((CheckAvailability)event,price);
		});
		subscribeEvent((new TakeBookEvent()).getClass(),(event)->{
			OrderResult result = inventory.take(event.getBook()); //Do take action from inventory
			this.complete((TakeBookEvent)event,result);
		});
		subscribeBroadcast((new FinalTickBroadcast()).getClass(), (event) -> {
			this.terminate(); //Kill this Service
		});
	}

}
