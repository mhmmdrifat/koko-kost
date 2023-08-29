package id.ac.ui.cs.advprog.kost.teman_menginap.controller;

import id.ac.ui.cs.advprog.kost.core.model.JwtPayload;
import id.ac.ui.cs.advprog.kost.teman_menginap.dto.CreateTemanMenginapRequest;
import id.ac.ui.cs.advprog.kost.teman_menginap.model.TemanMenginap;
import id.ac.ui.cs.advprog.kost.teman_menginap.service.TemanMenginapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/kost/temanMenginap")
public class TemanMenginapController {

    @Autowired
    private TemanMenginapService temanMenginapService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('PENGELOLA')")
    public ResponseEntity<List<TemanMenginap>> getAllDaftarRiwayat() {
        var response = temanMenginapService.findAll();
        return ResponseEntity.ok(response);    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('PELANGGAN')")
    public ResponseEntity<List<TemanMenginap>> getMyDaftarRiwayat() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = ((JwtPayload) authentication.getCredentials()).getUserId();
        var response = temanMenginapService.findAllByTenantId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PENGELOLA') || hasAuthority('PELANGGAN')")
    public ResponseEntity<TemanMenginap> getDaftarRiwayatById(@PathVariable Integer id) {
        var response = temanMenginapService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PELANGGAN')")
    public ResponseEntity<TemanMenginap> createTemanMenginap(@RequestBody CreateTemanMenginapRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = ((JwtPayload) authentication.getCredentials()).getUserId();
        request.setUserId(userId);
        var response = temanMenginapService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PENGELOLA')")
    public ResponseEntity<String> deleteTemanMenginap(@PathVariable Integer id) {
        temanMenginapService.delete(id);
        return ResponseEntity.ok(String.format("Deleted Teman Menginap Request with id %d", id));
    }
}
