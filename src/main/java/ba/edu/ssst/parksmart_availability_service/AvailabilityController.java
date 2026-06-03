package ba.edu.ssst.parksmart_availability_service;

import org.springframework.web.bind.annotation.*;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.List;
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

    private int getTotalCapacity(Long parkingId) {
        Map<Long, Integer> capacities = Map.of(
                1L, 80,
                2L, 50,
                3L, 40,
                4L, 120,
                5L, 60,
                6L, 200
        );
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

    @GetMapping("/all")
    public List<Map<String, Object>> getAllAvailability() {
        List<Long> parkingIds = List.of(1L, 2L, 3L, 4L, 5L, 6L);
        return parkingIds.stream()
                .map(this::getAvailability)
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        return response;
    }

}