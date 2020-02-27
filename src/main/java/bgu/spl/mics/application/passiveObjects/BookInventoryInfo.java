package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 */

public class BookInventoryInfo implements Serializable {

	private String BookTitle;
	private int AmountInInventory;
	private int Price;

	/**
	 * Constructor
	 * @param title
	 * @param amount
	 * @param price
	 */
	public BookInventoryInfo(String title ,int amount, int price){
		BookTitle=title;
		AmountInInventory=amount;
		Price=price;
	}

	/**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.   
     */
	public String getBookTitle() {
	return BookTitle;
	}

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.      
     */
	public int getAmountInInventory() {
		return AmountInInventory;
	}

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice() {
		return Price;
	}

	/**
	 * Updates the amount of the book in The inventory.
	 * Reduces the amount by 1.
	 */
	public void setAmountInInventory() {
		AmountInInventory--;
	}
}
