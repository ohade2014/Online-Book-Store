package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a receipt that should 
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt implements Serializable {
	int OrderId;
	String Seller;
	int Customer;
	String BookTitle;
	int Price;
	int IssuedTick;
	int OrderTick;
	int ProccessTick;

	/**
	 * Constructor
	 * @param id
	 * @param seller
	 * @param Customer
	 * @param BookTitle
	 * @param price
	 * @param IssuedTick
	 * @param OrderTick
	 * @param ProccessTick
	 */
	public OrderReceipt(int id, String seller, int Customer, String BookTitle, int price, int IssuedTick, int OrderTick,int ProccessTick){
		OrderId=id;
		Seller=seller;
		this.Customer=Customer;
		this.BookTitle=BookTitle;
		this.Price=price;
		this.IssuedTick=IssuedTick;
		this.OrderTick=OrderTick;
		this.ProccessTick=ProccessTick;
	}

	/**
     * Retrieves the orderId of this receipt.
     */
	public int getOrderId() {
		return OrderId;
	}
	
	/**
     * Retrieves the name of the selling service which handled the order.
     */
	public String getSeller() {
		return Seller;
	}
	
	/**
     * Retrieves the ID of the customer to which this receipt is issued to.
     * <p>
     * @return the ID of the customer
     */
	public int getCustomerId() {
		return Customer;
	}
	
	/**
     * Retrieves the name of the book which was bought.
     */
	public String getBookTitle() {
		return BookTitle;
	}
	
	/**
     * Retrieves the price the customer paid for the book.
     */
	public int getPrice() {
		return Price;
	}
	
	/**
     * Retrieves the tick in which this receipt was issued.
     */
	public int getIssuedTick() {
		return IssuedTick;
	}
	
	/**
     * Retrieves the tick in which the customer sent the purchase request.
     */
	public int getOrderTick() {
		return OrderTick;
	}
	
	/**
     * Retrieves the tick in which the treating selling service started 
     * processing the order.
     */
	public int getProcessTick() {
		return ProccessTick;
	}
}
