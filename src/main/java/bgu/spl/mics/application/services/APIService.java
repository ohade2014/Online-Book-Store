package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.OrderBookEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link OrderBookEvent}.
 */
public class APIService extends MicroService{

	private ConcurrentHashMap<Integer, LinkedList <OrderBookEvent>> OrderSchedule = new ConcurrentHashMap<Integer, LinkedList <OrderBookEvent>>();
	private Integer CurrentTick;
	private Customer customer;
	private LinkedBlockingQueue <Future<OrderReceipt>> receipts = new LinkedBlockingQueue<>();

	/**
	 * Empty Constructor
	 */
	public APIService() {
		super("APIService");
	}

	/**
	 * Constructor
	 * @param schedule
	 * @param customer
	 * @param nameService
	 */
	public APIService(LinkedList <OrderBookEvent> schedule , Customer customer, String nameService){
		super(nameService);
		ListIterator <OrderBookEvent> listIterator = schedule.listIterator();
		while (listIterator.hasNext()) {
			OrderBookEvent order = listIterator.next();
			if (OrderSchedule.get(order.getOrderTick()) == null) {
				LinkedList <OrderBookEvent> list = new LinkedList<>();
				list.add(order);
				OrderSchedule.put(order.getOrderTick(), list);
			}
			else{
				OrderSchedule.get(order.getOrderTick()).add(order);
			}
		}
		this.customer = customer;
	}


	@Override
	/**
	 * Defines Which Events and Broadcasts this service will be subscribed to.
	 */
	protected void initialize() {
		subscribeBroadcast(new TickBroadcast().getClass(), (event) ->{
			CurrentTick = ((TickBroadcast) event).getTick();
			ListIterator <OrderBookEvent> listIterator = OrderSchedule.get(CurrentTick).listIterator();

			while (listIterator.hasNext()){ //Send the orders that should be sent in this tick due to the Order Schedule
				Future<OrderReceipt> receipt = sendEvent(listIterator.next());
				if (receipt != null)
				    receipts.add(receipt);
			}

			Iterator <Future<OrderReceipt>> ReceiptsIter = receipts.iterator();
			while (ReceiptsIter.hasNext()) { //Check all orders status
				Future<OrderReceipt> rec = ReceiptsIter.next();
				if(rec.isDone()){ //If order is done, Enter receipt to customer's list and remove it from the future's list.
					if (rec.get() != null) {
						this.customer.InsertReceipt(rec.get());
						receipts.remove(rec);
					}
				}
			}
		});
		subscribeBroadcast((new FinalTickBroadcast()).getClass(), (event) -> {
			Iterator <Future<OrderReceipt>> iter = receipts.iterator();
			while(iter.hasNext()){ // check if there are receipts left in future's list that did not resolved yet.
				Future <OrderReceipt> rcpt = iter.next();
				if (rcpt.isDone() && rcpt.get() != null){ // If future is Done and did not resolved yet
					this.customer.InsertReceipt(rcpt.get());
					receipts.remove(rcpt);
				}
				else{ //Future is not done, resolve it to null
					rcpt.resolve(null);
				}
			}
			this.terminate(); //kill this Service
		});
	}

}
