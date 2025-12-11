package com.mangomusic.controller;

import com.mangomusic.model.ReportResult;
import com.mangomusic.service.ReportsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private final ReportsService reportsService;

    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @GetMapping("/daily-active-users")
    public ResponseEntity<List<ReportResult>> getDailyActiveUsers() {
        return ResponseEntity.ok(reportsService.getDailyActiveUsersReport());
    }

    @GetMapping("/top-albums-month")
    public ResponseEntity<List<ReportResult>> getTopAlbumsThisMonth() {
        return ResponseEntity.ok(reportsService.getTopAlbumsThisMonthReport());
    }

    @GetMapping("/genre-popularity")
    public ResponseEntity<List<ReportResult>> getGenrePopularity() {
        return ResponseEntity.ok(reportsService.getGenrePopularityReport());
    }

    @GetMapping("/top-artists")
    public ResponseEntity<List<ReportResult>> getTopArtists() {
        return ResponseEntity.ok(reportsService.getTopArtistsReport());
    }

    @GetMapping("/mapped/{userId}")
    public ResponseEntity<ReportResult> getMangoMusicMapped(@PathVariable int userId) {
        try {
            return ResponseEntity.ok(reportsService.getMangoMusicMapped(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}