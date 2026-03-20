import java.util.*;

// Custom Exception
class BookingException extends Exception {
    public BookingException(String message) {
        super(message);
    }
}

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

// Inventory
class Inventory {
    private Map<String, Integer> availability = new HashMap<>();

    public void addRoom(String type, int count) {
        availability.put(type, count);
    }

    public synchronized void allocateRoom(String type) throws BookingException {
        int count = availability.getOrDefault(type, 0);

        if (count <= 0) {
            throw new BookingException("No rooms available for " + type);
        }

        availability.put(type, count - 1);
    }

    public synchronized void releaseRoom(String type) {
        int count = availability.getOrDefault(type, 0);
        availability.put(type, count + 1);
    }

    public int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }
}

// Reservation
class Reservation {
    private String id;
    private Room room;
    private boolean isActive;

    public Reservation(String id, Room room) {
        this.id = id;
        this.room = room;
        this.isActive = true;
    }

    public String getId() { return id; }
    public Room getRoom() { return room; }
    public boolean isActive() { return isActive; }

    public void cancel() {
        isActive = false;
    }

    public void display() {
        System.out.println("Reservation ID: " + id);
        System.out.println("Room: " + room.getType());
        System.out.println("Status: " + (isActive ? "Active" : "Cancelled"));
        System.out.println("----------------------");
    }
}

// Booking Service
class BookingService {
    private Inventory inventory;
    private Map<String, Room> roomCatalog;
    private Map<String, Reservation> reservations = new HashMap<>();

    public BookingService(Inventory inventory, Map<String, Room> roomCatalog) {
        this.inventory = inventory;
        this.roomCatalog = roomCatalog;
    }

    // Booking
    public String book(String roomType) {
        try {
            inventory.allocateRoom(roomType);

            String id = "RES" + System.currentTimeMillis();
            Reservation r = new Reservation(id, roomCatalog.get(roomType));
            reservations.put(id, r);

            System.out.println("✅ Booking Successful: " + id);
            return id;

        } catch (Exception e) {
            System.out.println("❌ Booking Failed: " + e.getMessage());
            return null;
        }
    }

    // Cancellation + Rollback
    public void cancelBooking(String reservationId) {
        Reservation r = reservations.get(reservationId);

        if (r == null) {
            System.out.println("❌ Invalid Reservation ID");
            return;
        }

        if (!r.isActive()) {
            System.out.println("❌ Booking already cancelled");
            return;
        }

        // Rollback inventory
        inventory.releaseRoom(r.getRoom().getType());

        // Mark as cancelled
        r.cancel();

        System.out.println("✅ Booking Cancelled: " + reservationId);
    }

    public void showAll() {
        for (Reservation r : reservations.values()) {
            r.display();
        }
    }
}

// Main Class (IMPORTANT)
public class BookMyStay {
    public static void main(String[] args) {

        // Setup
        Inventory inventory = new Inventory();
        inventory.addRoom("Single", 1);

        Map<String, Room> roomCatalog = new HashMap<>();
        roomCatalog.put("Single", new Room("Single", 2000));

        BookingService service = new BookingService(inventory, roomCatalog);

        // Step 1: Book
        String resId = service.book("Single");

        // Step 2: Cancel
        service.cancelBooking(resId);

        // Step 3: Try cancel again (edge case)
        service.cancelBooking(resId);

        // Step 4: Check inventory rollback
        System.out.println("Available Single Rooms: " + inventory.getAvailability("Single"));

        // Step 5: Show all bookings
        service.showAll();
    }
}