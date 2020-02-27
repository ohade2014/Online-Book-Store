
//package test.java;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class InventoryTest {
    private Inventory inventory;
    private BookInventoryInfo[] books;
    @Before
    public void setUp() throws Exception {
        inventory=Inventory.getInstance();
        books=new BookInventoryInfo[5];
        for(int i=0;i<books.length;i++){
            books[i]=new BookInventoryInfo("book"+i+" test ",(i+1)*10,(i+1)*100);
        }
        inventory.load(books);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getInstance() {
        Inventory temp = Inventory.getInstance();
        assertEquals(inventory, temp);
    }
    @Test
    public void load() {
        for (int i = 0 ; i < books.length ; i++)
            assertEquals(inventory.checkAvailabiltyAndGetPrice(books[i].getBookTitle()), books[i].getPrice());
    }

    @Test
    public void take() {
        //inventory.load(books);
        try{
            inventory.take(null);
            fail("Book To Take is null!");
        }
        catch (Exception e){
        }
        assertEquals(inventory.take(books[1].getBookTitle()), OrderResult.SUCCESSFULLY_TAKEN);
        assertEquals(inventory.take("Lord Of The Ring"), OrderResult.NOT_IN_STOCK);
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        //inventory.load(books);
        try{
            inventory.checkAvailabiltyAndGetPrice(null);
            fail("Book To Take is null!");
        }
        catch (Exception e){
        }
        for(int i=0;i<books.length;i++){
            assertEquals(inventory.checkAvailabiltyAndGetPrice(books[i].getBookTitle()), books[i].getPrice());
        }
        assertEquals(inventory.checkAvailabiltyAndGetPrice("Lord Of The Ring"), -1);

    }

    @Test
    public void printInventoryToFile() {
    }
}