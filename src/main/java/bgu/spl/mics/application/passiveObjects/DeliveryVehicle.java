package bgu.spl.mics.application.passiveObjects;

import java.util.List;

/**
 * Passive data-object representing a delivery vehicle of the store.
 */

public class DeliveryVehicle {
	int License;
	int Speed;

	/**
     * Constructor.   
     */
	 public DeliveryVehicle(int license, int speed) {
		License=license;
		Speed=speed;
	 }

	/**
     * Retrieves the license of this delivery vehicle.   
     */
	public int getLicense() {
		return License;
	}
	
	/**
     * Retrieves the speed of this vehicle person.   
     * <p>
     * @return Number of ticks needed for 1 Km.
     */
	public int getSpeed() {
		return Speed;
	}
	
	/**
     * Simulates a delivery by sleeping for the amount of time that 
     * it takes this vehicle to cover {@code distance} KMs.  
     * <p>
     * @param address	The address of the customer.
     * @param distance	The distance from the store to the customer.
     */
	public void deliver(String address, int distance) {
		try{
			Thread.currentThread().sleep(distance/getSpeed());
		}
		catch(Exception e){}
	}
}
