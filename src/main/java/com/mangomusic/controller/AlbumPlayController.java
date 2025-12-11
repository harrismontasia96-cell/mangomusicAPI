package com.mangomusic.controller;

import com.mangomusic.model.AlbumPlay;
import com.mangomusic.service.AlbumPlayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plays")
public class AlbumPlayController {

    private final AlbumPlayService albumPlayService;

    public AlbumPlayController(AlbumPlayService albumPlayService) {
        this.albumPlayService = albumPlayService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AlbumPlay>> getUserRecentPlays(
            @PathVariable int userId,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(albumPlayService.getUserRecentPlays(userId, limit));
    }

    @GetMapping("/album/{albumId}")
    public ResponseEntity<List<AlbumPlay>> getAlbumPlays(@PathVariable int albumId) {
        return ResponseEntity.ok(albumPlayService.getAlbumPlays(albumId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumPlay> getPlayById(@PathVariable long id) {
        AlbumPlay play = albumPlayService.getPlayById(id);
        if (play == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(play);
    }

    @PostMapping
    public ResponseEntity<AlbumPlay> createPlay(@RequestBody AlbumPlay play) {
        try {
            AlbumPlay created = albumPlayService.createPlay(play);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlay(@PathVariable long id) {
        boolean deleted = albumPlayService.deletePlay(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}