package id.ac.ui.cs.advprog.kost.rent.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import id.ac.ui.cs.advprog.kost.core.exceptions.InvalidDateException;
import id.ac.ui.cs.advprog.kost.core.model.JwtPayload;
import id.ac.ui.cs.advprog.kost.rent.dto.KostRentRequest;
import id.ac.ui.cs.advprog.kost.rent.model.KostRent;
import id.ac.ui.cs.advprog.kost.rent.service.KostRentService;

import jakarta.validation.*;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(path = { "/kost/rent" })
@RequiredArgsConstructor
public class KostRentController {
    private final KostRentService kostRentService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('PENGELOLA')")
    public ResponseEntity<List<KostRent>> getAllKostRent() {
        List<KostRent> response = kostRentService.findAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('PELANGGAN')")
    public ResponseEntity<List<KostRent>> getMyKostRent() {

        Integer userId = getUserId();
        List<KostRent> response;
        response = kostRentService.findAllByTenantId(userId);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PENGELOLA') || hasAuthority('PELANGGAN')")
    public ResponseEntity<KostRent> getKostRentById(@PathVariable Integer id) {
        KostRent response = kostRentService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PELANGGAN')")
    public ResponseEntity<KostRent> addKostRent(@Valid @RequestBody KostRentRequest request) {
        validateDate(request);
        Integer userId = getUserId();
        String userName = getUsername();
        request.setUserId(userId);
        request.setUserName(userName);

        KostRent response = kostRentService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('PENGELOLA')")
    public ResponseEntity<String> deleteKostRent(@PathVariable Integer id) {
        kostRentService.delete(id);
        return ResponseEntity.ok(String.format("Deleted Kost Rent with id %d", id));
    }

    private Integer getUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((JwtPayload) authentication.getCredentials()).getUserId();

    }

    private String getUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        return ((JwtPayload) authentication.getCredentials()).getName();

    }

    private void validateDate(KostRentRequest request) {
        if (!request.getCheckInDate().toString().matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new InvalidDateException("checkInDate");
        }
        if (!request.getCheckOutDate().toString().matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new InvalidDateException("checkOutDate");
        }
    }

}
