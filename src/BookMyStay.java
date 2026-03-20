import java.util.*;

// Room Model
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

// Add-On Service
class AddOnService {
    private String name;
    private double price;

    public AddOnService(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
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

// Reservation
class Reservation {
    private String id;
    private Room room;
    private List<AddOnService> services = new ArrayList<>();

    public Reservation(String id, Room room) {
        this.id = id;
        this.room = room;
    }

    public void addService(AddOnService s) {
        services.add(s);
    }

    public double getTotalCost() {
        double total = room.getPrice();
        for (AddOnService s : services) {
            total += s.getPrice();
        }
        return total;
    }

    public String getId() { return id; }

    public void display() {
        System.out.println("Reservation ID: " + id);
        System.out.println("Room: " + room.getType());
        System.out.println("Total Cost: ₹" + getTotalCost());
        System.out.println("----------------------");
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

    public Reservation book(String roomType) {
        if (inventory.allocateRoom(roomType)) {
            String id = "RES" + System.currentTimeMillis();
            Reservation r = new Reservation(id, roomCatalog.get(roomType));
            reservations.put(id, r);
            return r;
        }
        return null;
    }

    public Collection<Reservation> getAllReservations() {
        return reservations.values();
    }
}

// Reporting Service (NEW)
class ReportingService {
    public void showReport(Collection<Reservation> reservations) {
        int totalBookings = reservations.size();
        double totalRevenue = 0;

        System.out.println("📊 Booking Report");
        System.out.println("----------------------");

        for (Reservation r : reservations) {
            r.display();
            totalRevenue += r.getTotalCost();
        }

        System.out.println("Total Bookings: " + totalBookings);
        System.out.println("Total Revenue: ₹" + totalRevenue);
    }
}

// Main Class
public class BookMyStay {
    public static void main(String[] args) {

        // Setup
        Inventory inventory = new Inventory();
        inventory.addRoom("Single", 2);

        Map<String, Room> roomCatalog = new HashMap<>();
        roomCatalog.put("Single", new Room("Single", 2000));

        BookingService bookingService = new BookingService(inventory, roomCatalog);

        // Simulate bookings
        Reservation r1 = bookingService.book("Single");
        Reservation r2 = bookingService.book("Single");

        // Reporting
        ReportingService report = new ReportingService();
        report.showReport(bookingService.getAllReservations());
    }
}