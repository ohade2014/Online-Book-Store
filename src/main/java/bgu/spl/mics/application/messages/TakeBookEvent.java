package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class TakeBookEvent <OrderResult> implements Event {
    private String Book;

    /**
     * Constructor
     * @param Book
     */
    public TakeBookEvent(String Book){
        this.Book=Book;
    }

    /**
     * Empty Constructor
     */
    public TakeBookEvent(){
        this.Book="";
    }

    /**
     *
     * @return Book name
     */
    public String getBook(){
        return Book;
    }
}
