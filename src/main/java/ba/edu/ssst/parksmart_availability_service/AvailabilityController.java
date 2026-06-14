package ba.edu.ssst.parksmart_availability_service;

import org.springframework.web.bind.annotation.*;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/availability")
@CrossOrigin(origins = "*")
public class AvailabilityController {

    private final Random random = new Random();

    @GetMapping("/{parkingId}")
    public Map<String, Object> getAvailability(@PathVariable Long parkingId) {
        int totalCapacity = getTotalCapacity(parkingId);
        int availableSpots = calculateAvailableSpots(parkingId, totalCapacity);
        String status = calculateStatus(availableSpots, totalCapacity);

        Map<String, Object> response = new HashMap<>();
        response.put("parkingId", parkingId);
        response.put("availableSpots", availableSpots);
        response.put("totalCapacity", totalCapacity);
        response.put("status", status);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @GetMapping("/all")
    public List<Map<String, Object>> getAllAvailability() {
        List<Long> parkingIds = List.of(
                1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L,
                11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L,
                21L, 22L, 23L, 24L, 25L, 26L, 27L, 28L, 29L, 30L, 31L
        );
        return parkingIds.stream()
                .map(this::getAvailability)
                .collect(Collectors.toList());
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        return response;
    }

    private int getTotalCapacity(Long parkingId) {
        Map<Long, Integer> capacities = new HashMap<>();
        capacities.put(1L, 200);   // Skenderija
        capacities.put(2L, 150);   // Vijećnica
        capacities.put(3L, 1000);  // SCC
        capacities.put(4L, 500);   // ARIA Mall
        capacities.put(5L, 80);    // Dom Armije
        capacities.put(6L, 300);   // Importanne
        capacities.put(7L, 200);   // Avaz Twist Tower
        capacities.put(8L, 400);   // Alta Shopping
        capacities.put(9L, 500);   // Aerodrom ZONA
        capacities.put(10L, 150);  // Hotel Holiday
        capacities.put(11L, 120);  // Marijin Dvor
        capacities.put(12L, 150);  // Hotel Holiday 2
        capacities.put(13L, 200);  // Sarajevo Tower
        capacities.put(14L, 200);  // Avaz
        capacities.put(15L, 300);  // Merkur Otoka
        capacities.put(16L, 250);  // Bingo Otoka
        capacities.put(17L, 150);  // TC Konzum Koševo
        capacities.put(18L, 80);   // BOSMAN
        capacities.put(19L, 400);  // Alta Shopping
        capacities.put(20L, 200);  // Radon Plaza
        capacities.put(21L, 180);  // Hotel Hills
        capacities.put(22L, 300);  // Bingo City Center Ilidža
        capacities.put(23L, 200);  // Grand Centar Ilidža
        capacities.put(24L, 150);  // Terminal Ilidža
        capacities.put(25L, 500);  // ZONA Aerodrom
        capacities.put(26L, 300);  // Simply Parking Aerodrom
        capacities.put(27L, 250);  // Mercator Dobrinja
        capacities.put(28L, 100);  // Campus UNSA
        capacities.put(29L, 120);  // ASA Bolnica
        capacities.put(30L, 100);  // Željezničke stanica
        capacities.put(31L, 150);  // Autobuska stanica
        return capacities.getOrDefault(parkingId, 100);
    }

    private int calculateAvailableSpots(Long parkingId, int totalCapacity) {
        int hour = LocalTime.now().getHour();
        double occupancyRate = getOccupancyRate(hour);
        int baseAvailable = (int) (totalCapacity * (1 - occupancyRate));
        int randomVariation = random.nextInt(11) - 5;
        return Math.max(0, Math.min(totalCapacity, baseAvailable + randomVariation));
    }

    private double getOccupancyRate(int hour) {
        if (hour >= 0 && hour <= 6) return 0.10;
        if (hour >= 7 && hour <= 9) return 0.80;
        if (hour >= 10 && hour <= 12) return 0.70;
        if (hour >= 13 && hour <= 14) return 0.85;
        if (hour >= 15 && hour <= 17) return 0.90;
        if (hour >= 18 && hour <= 20) return 0.60;
        return 0.40;
    }

    private String calculateStatus(int availableSpots, int totalCapacity) {
        double freePercent = (double) availableSpots / totalCapacity;
        if (freePercent > 0.30) return "available";
        if (freePercent > 0) return "limited";
        return "full";
    }
}