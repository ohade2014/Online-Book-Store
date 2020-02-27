package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Passive object representing the resource manager.
 */
public class ResourcesHolder {
	private static ResourcesHolder instance = null;
	private LinkedBlockingQueue<DeliveryVehicle> Vehicles=new LinkedBlockingQueue<>();
	private LinkedBlockingQueue<Future<DeliveryVehicle>> vehicles_waiting=new LinkedBlockingQueue<>();
	private Semaphore semaphore;


	/**
     * Retrieves the single instance of this class.
     */
	public ResourcesHolder(){};

	/**
	 * Creates the single instance of this class
	 */
	private static class SingletonResourceHolder {
		private static ResourcesHolder instance = new ResourcesHolder();
	}

	/**
	 * 	Retrieves the single instance of this class
 	 */
	public static ResourcesHolder getInstance()
	{
		return SingletonResourceHolder.instance;
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		if (!semaphore.tryAcquire()){ // No vehicles are available
			Future <DeliveryVehicle> wait_vehicle = new Future<>();
			vehicles_waiting.add(wait_vehicle);
			return wait_vehicle; //Return future with no vehicle, vehicle will be resolved when some vehicle will be released
		}
		else{ //There is vehicle available
			DeliveryVehicle driver;
			driver=Vehicles.poll();
			Future <DeliveryVehicle> future= new Future<>();
			future.resolve(driver);
			return future;
		}
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		Future <DeliveryVehicle> is_wait = vehicles_waiting.poll();
		if (is_wait == null){ //If No Futures are waiting to get a vehicle
			Vehicles.add(vehicle);
			semaphore.release();
		}
		else{
			is_wait.resolve(vehicle);
		}
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		for(int i=0;i<vehicles.length;i++){ //Build vehicles
			try {
				Vehicles.put(vehicles[i]);
			}
			catch (Exception e){}
		}
		semaphore = new Semaphore(Vehicles.size()); //Create semaphore object that will manage how many threads will be responsible of taking vehicle
	}

}
