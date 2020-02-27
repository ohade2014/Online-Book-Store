package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.OpenTickTimeEvent;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * TimeService is the  global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private int Duration;
	private int Speed;
	int Counter=0;

	/**
	 * Constructor
	 */
	public TimeService() {
		super("TimeService");
	}

	/**
	 * Constructor
	 * @param duration
	 * @param speed
	 */
	public TimeService(int duration, int speed){
		super("TimeService");
		Duration=duration;
		Speed=speed;
	}

	@Override
	/**
	 * Start activating all the system.
	 * Sends tick broadcasts to services while waiting the Speed time defined.
	 * To kill all services, send Final Tick to all services.
	 */
	protected void initialize() {
		Counter = 1;
		while (Counter < Duration) { //Send all ticks in time
			TickBroadcast tick = new TickBroadcast(Counter);
			sendBroadcast(tick);
			try {
				Thread.sleep(Speed); //Wait the time defined to service in the constructor
			}
			catch (Exception e) {}
			Counter++;
		}
		FinalTickBroadcast final_tick = new FinalTickBroadcast();
		sendBroadcast(final_tick); //Send the final tick to all services, time has ended.
		terminate(); //Kill Service
	}
}
