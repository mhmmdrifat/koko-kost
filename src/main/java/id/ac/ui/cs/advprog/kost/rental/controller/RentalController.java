package id.ac.ui.cs.advprog.kost.rental.controller;

import java.util.List;

import id.ac.ui.cs.advprog.kost.core.model.JwtPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import id.ac.ui.cs.advprog.kost.rental.service.RentalService;
import id.ac.ui.cs.advprog.kost.rental.dto.Rental;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = { "/kost/rentals" })
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('PELANGGAN')")
    public ResponseEntity<List<Rental>> getAllRentalByUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = ((JwtPayload) authentication.getCredentials()).getUserId();
        List<Rental> response = rentalService.findAllByUserId(userId);
        return ResponseEntity.ok(response);
    }
}
