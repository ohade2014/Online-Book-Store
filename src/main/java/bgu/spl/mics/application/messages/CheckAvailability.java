package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class CheckAvailability <Integer> implements Event {
    private String Book;

    /**
     * @param Book
     * Constructor gets book name
     */
    public CheckAvailability(String Book){
        this.Book=Book;
    }

    /**
     * Empty Constructor
     */
    public CheckAvailability(){
        this.Book="";
    }

    /**
     * Return book name
     * @return Book
     */
    public String getBook(){
        return Book;
    }
}
