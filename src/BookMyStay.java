import java.io.*;
import java.util.*;

// Room Model
class Room implements Serializable {
    private String type;
    private double price;

    public Room(String type, double price) {
        this.type = type;
        this.price = price;
    }

    public String getType() { return type; }
    public double getPrice() { return price; }
}

// Reservation Model
class Reservation implements Serializable {
    private String id;
    private Room room;

    public Reservation(String id, Room room) {
        this.id = id;
        this.room = room;
    }

    public String getId() { return id; }

    public void display() {
        System.out.println("Reservation ID: " + id);
        System.out.println("Room: " + room.getType());
        System.out.println("Price: ₹" + room.getPrice());
        System.out.println("----------------------");
    }
}

// Persistence Service
class PersistenceService {
    private static final String FILE_NAME = "bookings.dat";

    // Save data to file
    public void save(Map<String, Reservation> data) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(data);
            System.out.println("💾 Data saved successfully");
        } catch (IOException e) {
            System.out.println("❌ Error saving data");
        }
    }

    // Load data from file
    public Map<String, Reservation> load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            System.out.println("🔄 Data loaded successfully");
            return (Map<String, Reservation>) in.readObject();
        } catch (Exception e) {
            System.out.println("⚠ No previous data found, starting fresh");
            return new HashMap<>();
        }
    }
}

// Booking Service
class BookingService {
    private Map<String, Room> roomCatalog;
    private Map<String, Reservation> reservations;
    private PersistenceService persistence;

    public BookingService(Map<String, Room> roomCatalog, PersistenceService persistence) {
        this.roomCatalog = roomCatalog;
        this.persistence = persistence;
        this.reservations = persistence.load(); // RECOVERY STEP
    }

    public void book(String roomType) {
        Room room = roomCatalog.get(roomType);

        if (room == null) {
            System.out.println("❌ Invalid room type");
            return;
        }

        String id = "RES" + System.currentTimeMillis();
        Reservation r = new Reservation(id, room);
        reservations.put(id, r);

        System.out.println("✅ Booking Done: " + id);
    }

    public void showAll() {
        for (Reservation r : reservations.values()) {
            r.display();
        }
    }

    public void shutdown() {
        persistence.save(reservations); // SAVE BEFORE EXIT
    }
}

// Main Class
public class BookMyStay {
    public static void main(String[] args) {

        // Setup
        Map<String, Room> roomCatalog = new HashMap<>();
        roomCatalog.put("Single", new Room("Single", 2000));

        PersistenceService persistence = new PersistenceService();
        BookingService service = new BookingService(roomCatalog, persistence);

        // Simulate booking
        service.book("Single");

        // Show history (includes previous runs)
        service.showAll();

        // Save before exit
        service.shutdown();
    }
}