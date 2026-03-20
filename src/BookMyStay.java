import java.util.*;

// Domain Model
class Room {
    private String type;
    private double price;

    public Room(String type, double price) {
        this.type = type;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }
}

// Inventory (State Holder - NOW MUTABLE)
class Inventory {
    private Map<String, Integer> availability = new HashMap<>();

    public void addRoom(String type, int count) {
        availability.put(type, count);
    }

    // synchronized for thread safety (basic concurrency control)
    public synchronized boolean bookRoom(String type) {
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

// Booking Service (Write operation)
class BookingService {
    private Inventory inventory;

    public BookingService(Inventory inventory) {
        this.inventory = inventory;
    }

    public void book(String roomType) {
        boolean success = inventory.bookRoom(roomType);

        if (success) {
            System.out.println("✅ Booking successful for: " + roomType);
        } else {
            System.out.println("❌ Booking failed. No rooms available for: " + roomType);
        }
    }
}

// Main Class
public class BookMyStay {
    public static void main(String[] args) {

        // Step 1: Setup Inventory
        Inventory inventory = new Inventory();
        inventory.addRoom("Single", 1);
        inventory.addRoom("Double", 1);

        // Step 2: Booking Service
        BookingService bookingService = new BookingService(inventory);

        // Step 3: Simulate multiple booking requests (First-Come-First-Served)
        bookingService.book("Single"); // should succeed
        bookingService.book("Single"); // should fail (no rooms left)

        bookingService.book("Double"); // succeed
        bookingService.book("Double"); // fail
    }
}