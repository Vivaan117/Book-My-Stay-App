import java.util.*;

// Domain Model: Room
class Room {
    private String type;
    private double price;
    private List<String> amenities;

    public Room(String type, double price, List<String> amenities) {
        this.type = type;
        this.price = price;
        this.amenities = amenities;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void displayDetails() {
        System.out.println("Room Type: " + type);
        System.out.println("Price: ₹" + price);
        System.out.println("Amenities: " + amenities);
        System.out.println("-----------------------------");
    }
}

// Inventory (State Holder - Read Only in this use case)
class Inventory {
    private Map<String, Integer> roomAvailability;

    public Inventory() {
        roomAvailability = new HashMap<>();
    }

    public void addRoom(String type, int count) {
        roomAvailability.put(type, count);
    }

    // Read-only access
    public int getAvailability(String type) {
        return roomAvailability.getOrDefault(type, 0);
    }

    public Map<String, Integer> getAllAvailability() {
        return Collections.unmodifiableMap(roomAvailability); // Defensive programming
    }
}

// Search Service (Read-only logic)
class SearchService {
    private Inventory inventory;
    private Map<String, Room> roomCatalog;

    public SearchService(Inventory inventory, Map<String, Room> roomCatalog) {
        this.inventory = inventory;
        this.roomCatalog = roomCatalog;
    }

    public void searchAvailableRooms() {
        System.out.println("Available Rooms:\n");

        for (String type : inventory.getAllAvailability().keySet()) {
            int available = inventory.getAvailability(type);

            // Validation: Only show rooms with availability > 0
            if (available > 0) {
                Room room = roomCatalog.get(type);

                if (room != null) { // Defensive check
                    room.displayDetails();
                    System.out.println("Available Count: " + available);
                    System.out.println("=============================");
                }
            }
        }
    }
}

// Main Class
public class UseCase4RoomSearch {
    public static void main(String[] args) {

        // Step 1: Create Inventory
        Inventory inventory = new Inventory();
        inventory.addRoom("Single", 5);
        inventory.addRoom("Double", 0);
        inventory.addRoom("Suite", 3);

        // Step 2: Create Room Catalog
        Map<String, Room> roomCatalog = new HashMap<>();

        roomCatalog.put("Single", new Room("Single", 2000,
                Arrays.asList("WiFi", "TV", "AC")));

        roomCatalog.put("Double", new Room("Double", 3500,
                Arrays.asList("WiFi", "TV", "AC", "Mini Bar")));

        roomCatalog.put("Suite", new Room("Suite", 6000,
                Arrays.asList("WiFi", "TV", "AC", "Mini Bar", "Jacuzzi")));

        // Step 3: Create Search Service
        SearchService searchService = new SearchService(inventory, roomCatalog);

        // Step 4: Guest searches for rooms (READ ONLY)
        searchService.searchAvailableRooms();
    }
}