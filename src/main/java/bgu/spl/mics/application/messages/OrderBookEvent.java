package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class OrderBookEvent <OrderReceipt> implements Event {
    private String BookName;
    private Customer customer;
    private int orderTick;

    /**
     * Empty Constructor
     */
    public OrderBookEvent(){
        customer=null;
        BookName="";
    }

    /**
     * Constructor
     * @param customer
     * @param Book
     * @param tick
     */
    public OrderBookEvent(Customer customer,String Book, int tick){
        this.customer=customer;
        BookName=Book;
        orderTick = tick;
    }

    /**
     * Get book's name
     * @return book's name
     */
    public String getBookname(){
        return BookName;
    }

    /**
     * Return Customer made the event
     * @return customer made the order
     */
    public Customer getCustomer(){
        return customer;
    }

    /**
     * Return tick in which the event was made
     * @return tick time of the event
     */
    public int getOrderTick(){return orderTick;}

}
