package id.ac.ui.cs.advprog.kost.order.controller;

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

import id.ac.ui.cs.advprog.kost.core.model.JwtPayload;
import id.ac.ui.cs.advprog.kost.order.dto.BundleOrderRequest;
import id.ac.ui.cs.advprog.kost.order.model.BundleOrder;
import id.ac.ui.cs.advprog.kost.order.service.BundleOrderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(path = { "/bundle/order" })
@RequiredArgsConstructor
public class BundleOrderController {
    private final BundleOrderService bundleOrderService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('PENGELOLA')")
    public ResponseEntity<List<BundleOrder>> getAllBundleOrder() {
        List<BundleOrder> response = bundleOrderService.findAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('PELANGGAN')")
    public ResponseEntity<List<BundleOrder>> getMyBundleOrder() {

        Integer userId = getUserId();
        List<BundleOrder> response = bundleOrderService.findAllByTenantId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PENGELOLA') || hasAuthority('PELANGGAN')")
    public ResponseEntity<BundleOrder> getBundleOrderById(@PathVariable Integer id) {
        BundleOrder response;
        response = bundleOrderService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PELANGGAN')")
    public ResponseEntity<BundleOrder> addBundleOrder(@RequestBody BundleOrderRequest request) {

        Integer userId = getUserId();
        request.setUserId(userId);
        BundleOrder response = bundleOrderService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('PENGELOLA')")
    public ResponseEntity<String> deleteBundleOrder(@PathVariable Integer id) {
        bundleOrderService.delete(id);
        return ResponseEntity.ok(String.format("Deleted Bundle Order with id %d", id));
    }

    private Integer getUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((JwtPayload) authentication.getCredentials()).getUserId();

    }

}
