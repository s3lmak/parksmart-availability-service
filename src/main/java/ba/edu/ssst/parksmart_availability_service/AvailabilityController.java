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
        return getAvailabilityWithCapacity(parkingId, 100);
    }

    @PostMapping("/batch")
    public List<Map<String, Object>> getAvailabilityBatch(@RequestBody List<Map<String, Object>> parkings) {
        return parkings.stream().map(parking -> {
            Long parkingId = ((Number) parking.get("id")).longValue();
            int totalCapacity = ((Number) parking.get("totalCapacity")).intValue();
            return getAvailabilityWithCapacity(parkingId, totalCapacity);
        }).collect(Collectors.toList());
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        return response;
    }

    private Map<String, Object> getAvailabilityWithCapacity(Long parkingId, int totalCapacity) {
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