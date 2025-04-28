package backend.controller;

import backend.model.Events;
import backend.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/events")
    public ResponseEntity<List<Events>> getEvents() {
        return ResponseEntity.ok(dashboardService.getAllEvents());
    }

    @GetMapping("/dashboard/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(dashboardService.getCategories());
    }

    @GetMapping("/dashboard/tags")
    public ResponseEntity<List<String>> getTags() {
        return ResponseEntity.ok(dashboardService.getTags());
    }
}
