package id.ac.ui.cs.advprog.kost.room.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import id.ac.ui.cs.advprog.kost.room.dto.KostRoomRequest;
import id.ac.ui.cs.advprog.kost.room.model.KostRoom;
import id.ac.ui.cs.advprog.kost.room.service.KostRoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = { "/kost" })
@RequiredArgsConstructor
public class KostRoomController {
    private final KostRoomService kostRoomService;

    @GetMapping("/all")
    public ResponseEntity<List<KostRoom>> getAllKostRoom() {

        List<KostRoom> response = kostRoomService.findAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KostRoom> getKostRoomById(@PathVariable Integer id) {
        KostRoom response = kostRoomService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PENGELOLA')")
    public ResponseEntity<KostRoom> addKostRoom(@RequestBody KostRoomRequest request) {
        KostRoom response = kostRoomService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/update/{id}")
    @PreAuthorize("hasAuthority('PENGELOLA')")
    public ResponseEntity<KostRoom> putKostRoom(@PathVariable Integer id, @RequestBody KostRoomRequest request) {
        KostRoom response = kostRoomService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('PENGELOLA')")
    public ResponseEntity<String> deleteKostRoom(@PathVariable Integer id) {
        kostRoomService.delete(id);
        return ResponseEntity.ok(String.format("Deleted Kost Room with id %d", id));
    }

}
