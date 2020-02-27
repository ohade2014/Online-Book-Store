package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 */

public class Customer implements Serializable {
	String name;
	int id;
	String address;
	int distance;
	List<OrderReceipt> Receipts;
	int CreditCard;
	int AvailableAmountInCreditCard;

	/**
	 * Constructor
	 * @param id
	 * @param name
	 * @param address
	 * @param distance
	 * @param CreditCard
	 * @param amount
	 */
	public Customer(int id , String name , String address , int distance , int CreditCard , int amount){
		this.name = name;
		this.id = id;
		this.address = address;
		this.distance = distance;
		this.CreditCard = CreditCard;
		this.AvailableAmountInCreditCard = amount;
		Receipts = new LinkedList<OrderReceipt>();
	}

	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return Receipts;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return AvailableAmountInCreditCard;
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return CreditCard;
	}

	/**
	 * Reduces the amount in customer's credit card by the parameter - amount
	 * @param amount
	 */
	public void setAmount(int amount){
		AvailableAmountInCreditCard=AvailableAmountInCreditCard-amount;
	}

	/**
	 * Insert Receipt parameter to receipts list of customer
	 * @param rec
	 */
	public void InsertReceipt (OrderReceipt rec){
		Receipts.add(rec);
	}
	
}
