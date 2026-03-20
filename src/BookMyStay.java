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

// Add-On Service Model
class AddOnService {
    private String name;
    private double price;

    public AddOnService(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}

// Inventory
class Inventory {
    private Map<String, Integer> availability = new HashMap<>();

    public void addRoom(String type, int count) {
        availability.put(type, count);
    }

    public synchronized boolean allocateRoom(String type) {
        int count = availability.getOrDefault(type, 0);

        if (count > 0) {
            availability.put(type, count - 1);
            return true;
        }
        return false;
    }
}

// Reservation Model
class Reservation {
    private String reservationId;
    private Room room;
    private List<AddOnService> addOns = new ArrayList<>();

    public Reservation(String reservationId, Room room) {
        this.reservationId = reservationId;
        this.room = room;
    }

    public void addService(AddOnService service) {
        addOns.add(service);
    }

    public double calculateTotal() {
        double total = room.getPrice();
        for (AddOnService s : addOns) {
            total += s.getPrice();
        }
        return total;
    }

    public void display() {
        System.out.println("Reservation ID: " + reservationId);
        System.out.println("Room: " + room.getType());
        System.out.println("Base Price: ₹" + room.getPrice());

        if (addOns.isEmpty()) {
            System.out.println("No Add-On Services Selected");
        } else {
            System.out.println("Add-Ons:");
            for (AddOnService s : addOns) {
                System.out.println(" - " + s.getName() + " (₹" + s.getPrice() + ")");
            }
        }

        System.out.println("Total Cost: ₹" + calculateTotal());
        System.out.println("----------------------------");
    }

    public String getReservationId() {
        return reservationId;
    }
}

// Booking Service
class BookingService {
    private Inventory inventory;
    private Map<String, Reservation> reservations = new HashMap<>();
    private Map<String, Room> roomCatalog;

    public BookingService(Inventory inventory, Map<String, Room> roomCatalog) {
        this.inventory = inventory;
        this.roomCatalog = roomCatalog;
    }

    public String confirmBooking(String roomType) {
        if (inventory.allocateRoom(roomType)) {
            String id = "RES" + System.currentTimeMillis();
            Reservation res = new Reservation(id, roomCatalog.get(roomType));
            reservations.put(id, res);

            System.out.println("✅ Booking Confirmed: " + id);
            return id;
        } else {
            System.out.println("❌ No rooms available for " + roomType);
            return null;
        }
    }

    public Reservation getReservation(String id) {
        return reservations.get(id);
    }
}

// Add-On Service Handler
class AddOnServiceManager {
    private Map<String, AddOnService> services = new HashMap<>();

    public AddOnServiceManager() {
        services.put("WiFi", new AddOnService("WiFi", 200));
        services.put("Food", new AddOnService("Food", 500));
        services.put("Spa", new AddOnService("Spa", 1000));
    }

    public AddOnService getService(String name) {
        return services.get(name);
    }
}

// Main Class (IMPORTANT)
public class BookMyStay {
    public static void main(String[] args) {

        // Setup Inventory
        Inventory inventory = new Inventory();
        inventory.addRoom("Single", 2);

        // Room Catalog
        Map<String, Room> roomCatalog = new HashMap<>();
        roomCatalog.put("Single", new Room("Single", 2000));

        // Services
        AddOnServiceManager serviceManager = new AddOnServiceManager();

        // Booking Service
        BookingService bookingService = new BookingService(inventory, roomCatalog);

        // Step 1: Book Room
        String reservationId = bookingService.confirmBooking("Single");

        if (reservationId != null) {
            // Step 2: Add Services
            Reservation res = bookingService.getReservation(reservationId);

            res.addService(serviceManager.getService("WiFi"));
            res.addService(serviceManager.getService("Food"));

            // Step 3: Display Final Bill
            res.display();
        }
    }
}