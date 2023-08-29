package id.ac.ui.cs.advprog.kost.occupancy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import id.ac.ui.cs.advprog.kost.occupancy.dto.Tenant;
import id.ac.ui.cs.advprog.kost.occupancy.dto.WithoutTenant;
import id.ac.ui.cs.advprog.kost.occupancy.service.OccupancyService;
import java.util.List;


@RestController
@RequestMapping(path = { "/kost" })
@RequiredArgsConstructor
public class OccupancyController {
    private final OccupancyService occupancyService;

    @GetMapping("tenants/all")
    @PreAuthorize("hasAuthority('PENGELOLA')")
    public ResponseEntity<List<Tenant>> getAllTenant() {
        List<Tenant> response = occupancyService.findAllTenant();
        return ResponseEntity.ok(response);
    }

    @GetMapping("tenants/all/{roomName}")
    @PreAuthorize("hasAuthority('PENGELOLA')")
    public ResponseEntity<List<Tenant>> getAllTenantByRoomName(@PathVariable String roomName) {
        List<Tenant> response = occupancyService.findAllTenantByRoomName(roomName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("without-tenants/all")
    @PreAuthorize("hasAuthority('PENGELOLA')")
    public ResponseEntity<List<WithoutTenant>> getAllWithoutTenant() {
        List<WithoutTenant> response = occupancyService.findAllWithoutTenant();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/without-tenants/all/{roomName}")
    @PreAuthorize("hasAuthority('PENGELOLA')")
    public ResponseEntity<List<WithoutTenant>> getAllWithoutTenantByRoomName(@PathVariable String roomName) {
        List<WithoutTenant> response = occupancyService.findAllWithoutTenantByRoomName(roomName);
        return ResponseEntity.ok(response);
    }
}
