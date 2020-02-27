package bgu.spl.mics.application;

import bgu.spl.mics.ServicesInitCounter;
import bgu.spl.mics.application.messages.OrderBookEvent;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {
        // Create instances of all singletons of the system
        Inventory inventory = Inventory.getInstance();
        MoneyRegister Cashier = MoneyRegister.getInstance();
        ResourcesHolder Resource = ResourcesHolder.getInstance();
        ServicesInitCounter servicesActivatedCounter = ServicesInitCounter.getInstance();
        int AllServicesCounter = 0;
        // Read input json file
        Gson gson = new Gson();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            HashMap details = gson.fromJson(reader,HashMap.class);

            // Initial Inventory
            ArrayList init_inventory = (ArrayList) details.get("initialInventory");
            BookInventoryInfo [] to_load = new BookInventoryInfo [init_inventory.size()];
            for (int i = 0 ; i < init_inventory.size() ; i++){
                LinkedTreeMap bookInfo = (LinkedTreeMap) init_inventory.get(i);
                String book_title = (String) bookInfo.get("bookTitle");
                int amount = ((Double) bookInfo.get("amount")).intValue();
                int price = ((Double) bookInfo.get("price")).intValue();
                BookInventoryInfo book = new BookInventoryInfo(book_title,amount,price);
                to_load[i] = book;
            }
            inventory.load(to_load);

            // Initial Resources Holder
            ArrayList init_resources = (ArrayList) details.get("initialResources");
            LinkedTreeMap vehicles_tree = (LinkedTreeMap) init_resources.get(0);
            ArrayList vehicles = (ArrayList) vehicles_tree.get("vehicles");
            DeliveryVehicle [] vehicles_to_load = new DeliveryVehicle [vehicles.size()];
            for (int i = 0 ; i < vehicles.size() ; i++){
                LinkedTreeMap vehicle = (LinkedTreeMap) vehicles.get(i);
                int license = ((Double) vehicle.get("license")).intValue();
                int speed = ((Double) vehicle.get("speed")).intValue();
                DeliveryVehicle car = new DeliveryVehicle(license,speed);
                vehicles_to_load[i] = car;
            }
            Resource.load(vehicles_to_load);

            //Initial Services
            LinkedTreeMap init_services = (LinkedTreeMap) details.get("services");

            //Start Time Service
            LinkedTreeMap init_time = (LinkedTreeMap) init_services.get("time");
            TimeService timeServiceRunnable = new TimeService(((Double) init_time.get("duration")).intValue(),((Double) init_time.get("speed")).intValue());
            Thread timeService = new Thread(timeServiceRunnable);
            AllServicesCounter++;

            //Start Selling Services
            int init_selling = ((Double) init_services.get("selling")).intValue();
            Thread [] SellingThreads = new Thread [init_selling];
            for (int i = 0 ; i < init_selling ; i++){
                SellingService sellingServiceRunnable = new SellingService ("Selling Service " + i);
                SellingThreads [i] = new Thread(sellingServiceRunnable);
                AllServicesCounter++;
                SellingThreads[i].start();
            }

            //Start Inventory Service
            int num_of_inventories = ((Double) init_services.get("inventoryService")).intValue();
            Thread [] InventoryThreads = new Thread [num_of_inventories];
            for (int i = 0 ; i < num_of_inventories ; i++){
                InventoryService inventoryRunnable = new InventoryService("Inventory Service " + i);
                InventoryThreads [i] = new Thread(inventoryRunnable);
                AllServicesCounter++;
                InventoryThreads[i].start();
            }

            //Start Logistics Service
            int num_of_logistics = ((Double) init_services.get("logistics")).intValue();
            Thread [] LogisticsThreads = new Thread [num_of_logistics];
            for (int i = 0 ; i < num_of_logistics ; i++){
                LogisticsService logisticRunnable = new LogisticsService("Logistics Service " + i);
                LogisticsThreads [i] = new Thread(logisticRunnable);
                AllServicesCounter++;
                LogisticsThreads[i].start();
            }

            //Start Resources Service
            int num_of_resources = ((Double) init_services.get("resourcesService")).intValue();
            Thread [] ResourcesThreads = new Thread [num_of_resources];
            for (int i = 0 ; i < num_of_resources ; i++){
                ResourceService ResourceRunnable = new ResourceService("Resource Service " + i);
                ResourcesThreads [i] = new Thread(ResourceRunnable);
                AllServicesCounter++;
                ResourcesThreads[i].start();
            }

            //Start Customers and API Services
            ArrayList CustomersInitial = (ArrayList) init_services.get("customers");
            Customer [] customers = new Customer[CustomersInitial.size()];
            Thread [] apiThreads = new Thread [customers.length];
            for (int i = 0 ; i < CustomersInitial.size() ; i++){
                // Build customer by read its details
                int customer_id = ((Double)((LinkedTreeMap) CustomersInitial.get(i)).get("id")).intValue();
                String customer_name = (String) ((LinkedTreeMap) CustomersInitial.get(i)).get("name");
                String customer_address = (String) ((LinkedTreeMap) CustomersInitial.get(i)).get("address");
                int customer_dist = ((Double)((LinkedTreeMap) CustomersInitial.get(i)).get("distance")).intValue();
                int customer_credit_card = ((Double)((LinkedTreeMap)((LinkedTreeMap) CustomersInitial.get(i)).get("creditCard")).get("number")).intValue();
                int customer_money_amount = ((Double)((LinkedTreeMap)((LinkedTreeMap) CustomersInitial.get(i)).get("creditCard")).get("amount")).intValue();
                customers[i] = new Customer(customer_id,customer_name,customer_address,customer_dist,customer_credit_card,customer_money_amount);
                // Customer created
                ArrayList schedule = (ArrayList) ((LinkedTreeMap) CustomersInitial.get(i)).get("orderSchedule");
                LinkedList <OrderBookEvent> linkedList_schedule = new LinkedList<>();
                for (int j = 0 ; j < schedule.size() ; j++){
                    OrderBookEvent orderBookEvent = new OrderBookEvent(customers[i],(String)((LinkedTreeMap) schedule.get(j)).get("bookTitle"),
                            ((Double)((LinkedTreeMap) schedule.get(j)).get("tick")).intValue());
                    linkedList_schedule.add(orderBookEvent);
                }
                apiThreads[i] = new Thread (new APIService(linkedList_schedule,customers[i], "API Service " + i));
                AllServicesCounter++;
            }
            //Start api threads
            for (int i = 0 ; i < apiThreads.length ; i++){
                apiThreads[i].start();
            }

            //Start time service only when all services were activated
            while (AllServicesCounter-1 != servicesActivatedCounter.getIntValue()){continue;}
            timeService.start();

            //Wait until all threads are done
            try{
                for (int i = 0 ; i < InventoryThreads.length ; i++){
                    InventoryThreads[i].join();
                }
                for (int i = 0 ; i < LogisticsThreads.length ; i++){
                    LogisticsThreads[i].join();
                }
                for (int i = 0 ; i < ResourcesThreads.length ; i++){
                    ResourcesThreads[i].join();
                }
                for (int i = 0 ; i < SellingThreads.length ; i++){
                    SellingThreads[i].join();
                }
                for (int i = 0 ; i < apiThreads.length ; i++){
                    apiThreads[i].join();
                }
                timeService.join();
            }
            catch (Exception e){}

            //Print Inventory HashMap
            inventory.printInventoryToFile(args[2]);

            //Print Order Receipts
            Cashier.printOrderReceipts(args[3]);

            //Create Print Customers HashMap
            HashMap<Integer,Customer> customersToPrint = new HashMap<>();
            for (int i = 0 ; i < customers.length ; i++){
                customersToPrint.put(customers[i].getId(),customers[i]);
            }

            try{
                // Print Customers
                FileOutputStream output = new FileOutputStream(args[1]);
                ObjectOutputStream object_output = new ObjectOutputStream(output);
                object_output.writeObject(customersToPrint);
                output.close();
                object_output.close();

                //Print MoneyRegister
                FileOutputStream output2 = new FileOutputStream(args[4]);
                ObjectOutputStream object_output2 = new ObjectOutputStream(output2);
                object_output2.writeObject(Cashier);
                output2.close();
                object_output2.close();
            }
            catch (IOException e){}
        }
        catch(IOException e){}
    }
}
