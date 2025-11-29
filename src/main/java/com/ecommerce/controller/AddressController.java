package com.ecommerce.controller;

import com.ecommerce.dto.AddressRequest;
import com.ecommerce.dto.AddressResponse;
import com.ecommerce.service.AddressService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Address Controller
 * Handles CRUD operations for user addresses.
 * HARD DELETE version.
 */
@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    // Constructor Injection
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    // NOTE: For now userId is passed as a query param or header
    // Later when JWT is added â†’ userId will come from token.

    // ------------------------------------------------------------
    // CREATE ADDRESS
    // ------------------------------------------------------------
    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(
            @RequestParam Long userId,    // temporary until JWT added
            @RequestBody AddressRequest request) {

        AddressResponse response = addressService.createAddress(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ------------------------------------------------------------
    // UPDATE ADDRESS
    // ------------------------------------------------------------
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @RequestParam Long userId,
            @PathVariable Long addressId,
            @RequestBody AddressRequest request) {

        AddressResponse response = addressService.updateAddress(userId, addressId, request);
        return ResponseEntity.ok(response);
    }

    // ------------------------------------------------------------
    // GET SINGLE ADDRESS
    // ------------------------------------------------------------
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponse> getAddress(
            @RequestParam Long userId,
            @PathVariable Long addressId) {

        AddressResponse response = addressService.getAddress(userId, addressId);
        return ResponseEntity.ok(response);
    }

    // ------------------------------------------------------------
    // GET ALL ADDRESSES OF USER
    // ------------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAllAddresses(@RequestParam Long userId) {
        List<AddressResponse> list = addressService.getAllAddresses(userId);
        return ResponseEntity.ok(list);
    }

    // ------------------------------------------------------------
    // DELETE ADDRESS (HARD DELETE)
    // ------------------------------------------------------------
    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> deleteAddress(
            @RequestParam Long userId,
            @PathVariable Long addressId) {

        boolean deleted = addressService.deleteAddress(userId, addressId);

        if (deleted) {
            return ResponseEntity.ok("Address deleted successfully.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete address.");
    }

    // ------------------------------------------------------------
    // SET DEFAULT ADDRESS
    // ------------------------------------------------------------
    @PatchMapping("/{addressId}/default")
    public ResponseEntity<String> setDefaultAddress(
            @RequestParam Long userId,
            @PathVariable Long addressId) {

        boolean updated = addressService.setDefaultAddress(userId, addressId);

        if (updated) {
            return ResponseEntity.ok("Default address updated successfully.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to set default address.");
    }
}