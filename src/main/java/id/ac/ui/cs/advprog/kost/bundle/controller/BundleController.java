package id.ac.ui.cs.advprog.kost.bundle.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import id.ac.ui.cs.advprog.kost.bundle.dto.BundleRequest;
import id.ac.ui.cs.advprog.kost.bundle.model.Bundle;
import id.ac.ui.cs.advprog.kost.bundle.service.BundleService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(path = { "/bundle" })
@RequiredArgsConstructor
public class BundleController {
    private final BundleService bundleService;

    @GetMapping("/all")
    public ResponseEntity<List<Bundle>> getAllBundle() {
        List<Bundle> response = null;
        response = bundleService.findAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bundle> getBundleById(@PathVariable Integer id) {
        Bundle response;
        response = bundleService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PENGELOLA')")
    public ResponseEntity<Bundle> addBundle(@RequestBody BundleRequest request) {
        Bundle response;
        response = bundleService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('PENGELOLA')")
    public ResponseEntity<String> deleteBundle(@PathVariable Integer id) {
        bundleService.delete(id);
        return ResponseEntity.ok(String.format("Deleted Bundle with id %d", id));
    }

}
