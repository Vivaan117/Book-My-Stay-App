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

// Inventory (State Holder)
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

    public int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }
}

// Reservation Model
class Reservation {
    private String reservationId;
    private String roomType;

    public Reservation(String reservationId, String roomType) {
        this.reservationId = reservationId;
        this.roomType = roomType;
    }

    public void display() {
        System.out.println("Reservation Confirmed!");
        System.out.println("Reservation ID: " + reservationId);
        System.out.println("Room Type: " + roomType);
        System.out.println("----------------------------");
    }
}

// Booking Service (Confirmation + Allocation)
class BookingService {
    private Inventory inventory;
    private Map<String, Reservation> reservations = new HashMap<>();

    public BookingService(Inventory inventory) {
        this.inventory = inventory;
    }

    public void confirmBooking(String roomType) {
        boolean allocated = inventory.allocateRoom(roomType);

        if (allocated) {
            String reservationId = generateReservationId();
            Reservation reservation = new Reservation(reservationId, roomType);

            reservations.put(reservationId, reservation);

            reservation.display();
        } else {
            System.out.println("❌ Booking Failed: No rooms available for " + roomType);
        }
    }

    private String generateReservationId() {
        return "RES" + System.currentTimeMillis();
    }
}

// Main Class (IMPORTANT: Your preferred name)
public class BookMyStay {
    public static void main(String[] args) {

        // Step 1: Setup Inventory
        Inventory inventory = new Inventory();
        inventory.addRoom("Single", 1);
        inventory.addRoom("Double", 1);

        // Step 2: Booking Service
        BookingService bookingService = new BookingService(inventory);

        // Step 3: Confirm Reservations
        bookingService.confirmBooking("Single"); // success
        bookingService.confirmBooking("Single"); // fail

        bookingService.confirmBooking("Double"); // success
    }
}