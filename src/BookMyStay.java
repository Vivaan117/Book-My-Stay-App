import java.util.*;

// Custom Exception
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
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

    public synchronized void allocateRoom(String type) throws InvalidBookingException {
        if (!availability.containsKey(type)) {
            throw new InvalidBookingException("Invalid room type: " + type);
        }

        int count = availability.get(type);

        if (count <= 0) {
            throw new InvalidBookingException("No rooms available for: " + type);
        }

        availability.put(type, count - 1);
    }
}

// Reservation
class Reservation {
    private String id;
    private Room room;

    public Reservation(String id, Room room) {
        this.id = id;
        this.room = room;
    }

    public void display() {
        System.out.println("Reservation ID: " + id);
        System.out.println("Room: " + room.getType());
        System.out.println("Price: ₹" + room.getPrice());
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

    public void book(String roomType) {
        try {
            validate(roomType);

            inventory.allocateRoom(roomType);

            String id = "RES" + System.currentTimeMillis();
            Reservation r = new Reservation(id, roomCatalog.get(roomType));
            reservations.put(id, r);

            System.out.println("✅ Booking Successful");
            r.display();

        } catch (InvalidBookingException e) {
            System.out.println("❌ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("⚠ Unexpected error occurred");
        }
    }

    // Validation Logic
    private void validate(String roomType) throws InvalidBookingException {
        if (roomType == null || roomType.trim().isEmpty()) {
            throw new InvalidBookingException("Room type cannot be empty");
        }

        if (!roomCatalog.containsKey(roomType)) {
            throw new InvalidBookingException("Room type does not exist");
        }
    }
}

// Main Class
public class BookMyStay {
    public static void main(String[] args) {

        // Setup
        Inventory inventory = new Inventory();
        inventory.addRoom("Single", 1);

        Map<String, Room> roomCatalog = new HashMap<>();
        roomCatalog.put("Single", new Room("Single", 2000));

        BookingService service = new BookingService(inventory, roomCatalog);

        // Test Cases
        service.book("Single");   // ✅ valid
        service.book("Single");   // ❌ no availability
        service.book("Double");   // ❌ invalid type
        service.book("");         // ❌ empty input
    }
}