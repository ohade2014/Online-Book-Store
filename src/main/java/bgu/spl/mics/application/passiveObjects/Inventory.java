package bgu.spl.mics.application.passiveObjects;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 */
public class Inventory implements Serializable {
	private ConcurrentHashMap<String,BookInventoryInfo> inventoryInfoHashMap=new ConcurrentHashMap<String,BookInventoryInfo>();
	private HashMap <String,Integer> inventoryToPrint=new HashMap<String,Integer>();

	/**
     * Creates the single instance of this class.
     */
	private static class SingletonInventory {
		private static Inventory instance = new Inventory();
	}

	/**
	 * private Empty Constructor
	 */
	private Inventory (){}

	/**
	 * @return The singleton instance of the inventory
	 */
	public static Inventory getInstance() {
		return SingletonInventory.instance;
	}
	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[ ] inventory ) {
		for (int i=0 ; i<inventory.length ; i++){
			inventoryInfoHashMap.put(inventory[i].getBookTitle(),inventory[i]);
		}
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
	public OrderResult take (String book) {
		if (book == null) {
			throw new IllegalArgumentException();
		}

		if ((inventoryInfoHashMap.get(book)) == null) { // Book not exists in inventory
			return OrderResult.NOT_IN_STOCK;
		}
		else {
			synchronized (inventoryInfoHashMap.get(book)) {
				if (inventoryInfoHashMap.get(book).getAmountInInventory() > 0) { //there are copies of the book left in inventory
					inventoryInfoHashMap.get(book).setAmountInInventory();
					return OrderResult.SUCCESSFULLY_TAKEN;
				}
				return OrderResult.NOT_IN_STOCK;
			}
		}
	}

	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
		if(book==null)
			throw new IllegalArgumentException();
		if (inventoryInfoHashMap.get(book)==null)
			return -1;
		synchronized (inventoryInfoHashMap.get(book)) {
			if (inventoryInfoHashMap.get(book) != null && inventoryInfoHashMap.get(book).getAmountInInventory() > 0) { //If there are copies available of the book in inventory
				return inventoryInfoHashMap.get(book).getPrice();
			}
			return -1;
		}
	}
	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a 
     * Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename) {
		for(String i : inventoryInfoHashMap.keySet()){ //Build HashMap will be printed to json file
			inventoryToPrint.put(i,inventoryInfoHashMap.get(i).getAmountInInventory());
		}
		try{
			FileOutputStream output = new FileOutputStream(filename);
			ObjectOutputStream object_output = new ObjectOutputStream(output);
			object_output.writeObject(inventoryToPrint);
			output.close();
			object_output.close();
		}
		catch (IOException e){}
	}
}
