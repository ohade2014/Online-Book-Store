package bgu.spl.mics.application.passiveObjects;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 */
public class MoneyRegister implements Serializable{
	private static MoneyRegister instance=null;
	private LinkedBlockingQueue <OrderReceipt> MoneyMaker= new LinkedBlockingQueue<>();
	private List <OrderReceipt> PrintReceipts = new LinkedList<>();
	private AtomicInteger totalEarnings= new AtomicInteger();

	/**
	 * Empty constructor
	 */
	private MoneyRegister(){}

	/**
	 * Creates the single instance of the Money Register.
	 */
	private static class SingletonMoneyRegister {
		private static MoneyRegister instance = new MoneyRegister();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static MoneyRegister getInstance() {
		return SingletonMoneyRegister.instance;
	}

	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		MoneyMaker.add(r);
		totalEarnings.set(totalEarnings.intValue()+r.Price);
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		synchronized (totalEarnings){
		return totalEarnings.intValue();
	}}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
		c.setAmount(amount);
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.
     */
	public void printOrderReceipts(String filename) {
		Iterator <OrderReceipt> iter = MoneyMaker.iterator();
		while (iter.hasNext()){ //Build List Object will be printed to the json file
			PrintReceipts.add(iter.next());
		}
		try{
			FileOutputStream output = new FileOutputStream(filename);
			ObjectOutputStream object_output = new ObjectOutputStream(output);
			object_output.writeObject(PrintReceipts);
			output.close();
			object_output.close();
		}
		catch (IOException e){}
	}
}
