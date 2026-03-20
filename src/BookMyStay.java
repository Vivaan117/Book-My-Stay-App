import java.util.*;

// Room Model
class Room {
    private String type;
    private double price;

    public Room(String type, double price) {
        this.type = type;
        this.price = price;
    }

    public String getType() { return type; }
    public double getPrice() { return price; }
}

// Inventory (Thread-Safe)
class Inventory {
    private Map<String, Integer> availability = new HashMap<>();

    public void addRoom(String type, int count) {
        availability.put(type, count);
    }

    // synchronized ensures only one thread accesses at a time
    public synchronized boolean allocateRoom(String type) {
        int count = availability.getOrDefault(type, 0);

        if (count > 0) {
            availability.put(type, count - 1);
            return true;
        }
        return false;
    }

    public int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }
}

// Booking Service
class BookingService {
    private Inventory inventory;

    public BookingService(Inventory inventory) {
        this.inventory = inventory;
    }

    public void book(String user, String roomType) {
        boolean success = inventory.allocateRoom(roomType);

        if (success) {
            System.out.println("✅ " + user + " successfully booked " + roomType);
        } else {
            System.out.println("❌ " + user + " failed to book " + roomType + " (Sold Out)");
        }
    }
}

// Thread Class (Simulates User)
class BookingTask implements Runnable {
    private BookingService service;
    private String user;
    private String roomType;

    public BookingTask(BookingService service, String user, String roomType) {
        this.service = service;
        this.user = user;
        this.roomType = roomType;
    }

    @Override
    public void run() {
        service.book(user, roomType);
    }
}

// Main Class
public class BookMyStay {
    public static void main(String[] args) {

        // Setup
        Inventory inventory = new Inventory();
        inventory.addRoom("Single", 1); // Only 1 room

        BookingService service = new BookingService(inventory);

        // Simulate multiple users booking simultaneously
        Thread t1 = new Thread(new BookingTask(service, "User1", "Single"));
        Thread t2 = new Thread(new BookingTask(service, "User2", "Single"));
        Thread t3 = new Thread(new BookingTask(service, "User3", "Single"));

        // Start threads (concurrent execution)
        t1.start();
        t2.start();
        t3.start();

        // Wait for all threads to finish
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted");
        }

        // Final availability
        System.out.println("Remaining Rooms: " + inventory.getAvailability("Single"));
    }
}