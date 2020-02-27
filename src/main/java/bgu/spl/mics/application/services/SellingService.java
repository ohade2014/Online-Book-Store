package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link OrderBookEvent}.
 */
public class SellingService extends MicroService{
	private boolean is_charged = false;
	MoneyRegister Cashier = MoneyRegister.getInstance();
	private int CurrentTick;

	/**
	 * Constructor
	 * @param name
	 */
	public SellingService(String name) {
		super(name);
	}

	@Override
	/**
	 * Defines Which Events and Broadcasts this service will be subscribed to.
	 */
	protected void initialize() {
		subscribeEvent(new OrderBookEvent().getClass() , (event) -> {
			Future <Integer> FutureCheckAvailability = sendEvent(new CheckAvailability<Integer>(event.getBookname()));
			Future <OrderResult> FutureTakeBook=null;
			synchronized (event.getCustomer()){ //Make sure that each customer's order will be done properly, without wrong charges
				//If book is available and there is enough money to charge, do the take book action
				if (FutureCheckAvailability.get()!= null && (!FutureCheckAvailability.get().equals(-1) & event.getCustomer().getAvailableCreditAmount()>=FutureCheckAvailability.get())){
					FutureTakeBook = sendEvent(new TakeBookEvent<OrderResult>(event.getBookname()));
					if (FutureTakeBook.get() == OrderResult.SUCCESSFULLY_TAKEN){ //Charge if book has taken
							Cashier.chargeCreditCard(event.getCustomer(),FutureCheckAvailability.get());
							is_charged = true;
					}
					else{ //Take book did not succeeded
						complete((OrderBookEvent) event, null);
					}
				}
				else { //Take book did not succeeded
					complete((OrderBookEvent) event, null);
				}
			}

			OrderReceipt receipt;
			if(FutureTakeBook!=null && FutureTakeBook.get() == OrderResult.SUCCESSFULLY_TAKEN & is_charged){ //If taking book made successfully , start the Delivery action
				Future<Boolean> DeliveryFuture=sendEvent(new DeliveryEvent<>(event.getCustomer().getAddress(),event.getCustomer().getDistance()));
				receipt = new OrderReceipt(0,this.getName(),event.getCustomer().getId(),event.getBookname(),FutureCheckAvailability.get(),CurrentTick,event.getOrderTick(),CurrentTick);
				complete((OrderBookEvent)event,receipt);
				Cashier.file(receipt); //Add receipt to MoneyRegister
			}
			else{ //Delivery did not succeeded
				complete((OrderBookEvent)event,null);}
		});
		subscribeBroadcast(new TickBroadcast().getClass(), (event) ->{
			// Get current tick from time service and update field
			CurrentTick = ((TickBroadcast) event).getTick();
		});
		subscribeBroadcast((new FinalTickBroadcast()).getClass(), (event) -> {
			this.terminate(); //Kill this Service.
		});
	}

}
