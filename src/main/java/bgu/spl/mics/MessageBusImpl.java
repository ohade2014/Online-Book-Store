package bgu.spl.mics;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	ConcurrentHashMap<Class, Queue<MicroService>> EventSubScription=new ConcurrentHashMap<Class,Queue<MicroService>>();
	ConcurrentHashMap<Class, Queue<MicroService>> BroadcastSubScription=new ConcurrentHashMap<Class, Queue<MicroService>>();
	ConcurrentHashMap<Message,Future> ActiveMessages= new ConcurrentHashMap<>();
	ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> MicroServiceQueue=new ConcurrentHashMap<>();

	/**
	 * private Empty Constructor
	 */
	private MessageBusImpl(){}

	/**
	 * Creates the single instance of this class
	 */
	private static class SingletonMessageBus {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	@Override
	/**
	 * Add to the HashMap EventSubScription the MicroService that subscribed to the event "type" parameter
	 * @param type The type to subscribe to,
	 * @param m    The subscribing micro-service.
	 * @param <T>
	 */
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (type) { //Make sure that 2 different thread will not create new Queue for the same Event Type
			if (EventSubScription.get(type) == null) //If this type of event did not subscribed yet
				EventSubScription.put(type, new LinkedBlockingQueue<MicroService>());
			EventSubScription.get(type).add(m);
		}
	}

	@Override
	/**
	 * Add to the HashMap BroadcastSubscription the microService that subscribes to the broadcast of "type" parameter
	 * @param type 	The type to subscribe to.
	 * @param m    	The subscribing micro-service.
	 */
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (type) { //Make sure that 2 different thread will not create new Queue for the same Broadcast Type
			if (BroadcastSubScription.get(type) == null) { //If this type of broadcast had never been subscribed
				Queue<MicroService> vec = new LinkedBlockingQueue<>();
				BroadcastSubScription.put(type, vec);
			}
			BroadcastSubScription.get(type).add(m);
		}
	}

	@Override
	/**
	 * Signs that the event e completed and should return future with "result" parameter
	 * Activate function resolve of the future represents the event e.
	 * @param e      The completed event.
	 * @param result The resolved result of the completed event.
	 * @param <T>
	 */
	public <T> void complete(Event<T> e, T result) {
		ActiveMessages.get(e).resolve(result);
	}

	@Override
	/**
	 * Send Broadcast of type "b" to the right Service
	 * It will add the Broadcast to the mission's queue of the destination service
	 * @param b 	The message to added to the queues.
	 */
	public void sendBroadcast(Broadcast b) {
		Iterator iter = BroadcastSubScription.get(b.getClass()).iterator();
		while (iter.hasNext()){
			MicroServiceQueue.get(iter.next()).add(b);
		}
	}

	@Override
	/**
	 * Send Event of type "e" to the right Service
	 * It will add the Event to the mission's queue of the destination service
	 * It will add the event to the Active Messages HashMap
	 * @param e 	The message to added to the queues.
	 */
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> future=new Future< >();
		ActiveMessages.put(e,future);
		if (EventSubScription.get(e.getClass()).size() == 0) //If no MicroService subscribed to this type of event
			return null;
		synchronized (EventSubScription.get(e.getClass())) { //Make sure that no event will be sent to unregistered MS
			MicroService m = EventSubScription.get(e.getClass()).poll();
			if (m != null) {
				MicroServiceQueue.get(m).add(e);
				EventSubScription.get(e.getClass()).add(m);
			}
			else
				return null;
		}
		return ActiveMessages.get(e);
	}

	@Override
	/**
	 * "Connect" Micro Service m to the Message Bus
	 * @param m the micro-service to create a queue for.
	 */
	public void register(MicroService m) {
		MicroServiceQueue.put(m, new LinkedBlockingQueue<Message>());
	}

	/**
	 * Removes the message queue allocated to {@code m} via the call to
	 * {@link #register(bgu.spl.mics.MicroService)} and cleans all references
	 * related to {@code m} in this message-bus. If {@code m} was not
	 * registered, nothing should happen.
	 * <p>
	 * @param m the micro-service to unregister.
	 */
	public void unregister(MicroService m) {
		for (Class c : EventSubScription.keySet()){
			//Make sure that no Micro Service will be unregistered while event is sent to it
			synchronized (EventSubScription.get(c)) {
				Iterator<MicroService> micro = EventSubScription.get(c).iterator();
				while (micro.hasNext()) { //Remove Micro Service m from queues of event it had subscribed to
					MicroService service = micro.next();
					if (service.getName() == m.getName())
						EventSubScription.get(c).remove(service);
				}
			}
		}
		//If Messages inserted to the queue of MicroService after the final tick, resolve it to null
		Iterator <Message> iter = MicroServiceQueue.get(m).iterator();
		while (iter.hasNext()) {
			complete((Event) iter.next(), null);
		}
		//Remove micro service from broadcast it subscribed to
		for (Class c : BroadcastSubScription.keySet()){
			Iterator iter2 = BroadcastSubScription.get(c).iterator();
			while (iter2.hasNext()){
				MicroService service2 = (MicroService) iter2.next();
				if (service2.getName() == service2.getName())
					BroadcastSubScription.get(c).remove(m);
			}
		}
		//"Disconnect" the Micro Service m from Message bus
		MicroServiceQueue.remove(m);
	}

	@Override
	/**
	 * Using this method, a <b>registered</b> micro-service can take message
	 * from its allocated queue.
	 * This method is blocking meaning that if no messages
	 * are available in the micro-service queue it
	 * should wait until a message becomes available.
	 * The method should throw the {@link IllegalStateException} in the case
	 * where {@code m} was never registered.
	 * <p>
	 * @param m The micro-service requesting to take a message from its message
	 *          queue.
	 * @return The next message in the {@code m}'s queue (blocking).
	 * @throws InterruptedException if interrupted while waiting for a message
	 *                              to became available.
	 */
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (MicroServiceQueue.get(m) == null)
			throw new IllegalStateException("Micro Service was never registered");
		return MicroServiceQueue.get(m).take();
	}

	/**
	 * Retreives the single instance of this class
	 * @return MessageBus instance
	 */
	public static synchronized MessageBusImpl GetInstance(){
		return SingletonMessageBus.instance;
	}
}
