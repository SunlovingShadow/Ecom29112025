package com.ecommerce.service.impl;

import com.ecommerce.dto.InventoryResponse;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Inventory;
import com.ecommerce.repository.InventoryRepository;
import com.ecommerce.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class InventoryServiceImpl implements InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final InventoryRepository inventoryRepo;

    public InventoryServiceImpl(InventoryRepository inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
        log.info("InventoryService Initialized");
    }

    // ------------------------------------------------------------
    // GET INVENTORY BY PRODUCT
    // ------------------------------------------------------------
    @Override
    public InventoryResponse getInventory(Long productId) {
        Inventory inv = inventoryRepo.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        return mapToResponse(inv);
    }

    // ------------------------------------------------------------
    // CREATE OR INITIALIZE INVENTORY
    // ------------------------------------------------------------
    @Override
    @Transactional
    public InventoryResponse createOrInitInventory(Long productId, int quantity) {

        if (quantity < 0) {
            throw new BadRequestException("Initial quantity cannot be negative");
        }

        // if exists → update, else create
        Inventory existing = inventoryRepo.findByProductId(productId).orElse(null);

        if (existing == null) {
            inventoryRepo.createInventory(productId, quantity);
            log.info("Created inventory for product {}: quantity={}", productId, quantity);
        } else {
            existing.setQuantity(quantity);
            existing.setReserved(0);
            inventoryRepo.update(existing);
            log.info("Reset inventory for product {}: quantity={}", productId, quantity);
        }

        return getInventory(productId);
    }

    // ------------------------------------------------------------
    // ADD STOCK
    // ------------------------------------------------------------
    @Override
    @Transactional
    public InventoryResponse addStock(Long productId, int quantity) {

        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be > 0");
        }

        ensureInventoryExists(productId);

        boolean success = inventoryRepo.increaseStock(productId, quantity);
        if (!success) {
            throw new BadRequestException("Failed to increase stock");
        }

        log.info("Added {} stock to product {}", quantity, productId);
        return getInventory(productId);
    }

    // ------------------------------------------------------------
    // DECREASE STOCK
    // ------------------------------------------------------------
    @Override
    @Transactional
    public InventoryResponse decreaseStock(Long productId, int quantity) {

        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be > 0");
        }

        ensureInventoryExists(productId);

        boolean success = inventoryRepo.decreaseStock(productId, quantity);
        if (!success) {
            throw new BadRequestException("Not enough stock to decrease");
        }

        log.info("Decreased {} stock from product {}", quantity, productId);
        return getInventory(productId);
    }

    // ------------------------------------------------------------
    // RESERVE STOCK FOR CHECKOUT
    // ------------------------------------------------------------
    @Override
    @Transactional
    public InventoryResponse reserveStock(Long productId, int quantity) {

        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be > 0");
        }

        ensureInventoryExists(productId);

        boolean success = inventoryRepo.reserveStock(productId, quantity);
        if (!success) {
            throw new BadRequestException("Not enough available stock to reserve for product: " + productId);
        }

        log.info("Reserved {} stock for product {}", quantity, productId);
        return getInventory(productId);
    }

    // ------------------------------------------------------------
    // RELEASE RESERVED STOCK
    // ------------------------------------------------------------
    @Override
    @Transactional
    public InventoryResponse releaseReserved(Long productId, int quantity) {

        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be > 0");
        }

        ensureInventoryExists(productId);

        boolean success = inventoryRepo.releaseReservedStock(productId, quantity);
        if (!success) {
            throw new BadRequestException("Not enough reserved stock to release");
        }

        log.info("Released {} reserved stock for product {}", quantity, productId);
        return getInventory(productId);
    }

    // ------------------------------------------------------------
    // CONSUME RESERVED ON ORDER CONFIRM - GRACEFUL VERSION
    // ------------------------------------------------------------
    /**
     * Consume reserved stock when payment succeeds.
     * 
     * IMPORTANT: This method is GRACEFUL - it returns null instead of throwing
     * exceptions. This is intentional because:
     * 
     * 1. This is called AFTER payment succeeds
     * 2. Customer has already paid - we don't want to fail the payment
     * 3. Inventory issues should be logged for admin review, not cause payment failure
     * 
     * PROPAGATION.REQUIRES_NEW ensures this runs in its own transaction,
     * so any issues here won't rollback the parent payment transaction.
     * 
     * @param productId The product ID
     * @param quantity The quantity to consume
     * @return InventoryResponse if successful, null if any issue occurs
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public InventoryResponse consumeReservedOnOrder(Long productId, int quantity) {
        
        // Validate quantity - return null instead of throwing
        if (quantity <= 0) {
            log.warn("Invalid quantity {} for product {} - skipping consumption", quantity, productId);
            return null;
        }

        // Check if inventory exists - return null instead of throwing
        Inventory inv = inventoryRepo.findByProductId(productId).orElse(null);
        if (inv == null) {
            log.warn("No inventory record for product {} - skipping consumption", productId);
            return null;
        }

        // Check if we have enough reserved stock
        if (inv.getReserved() < quantity) {
            log.warn("Product {} has only {} reserved but trying to consume {} - skipping", 
                    productId, inv.getReserved(), quantity);
            return null;
        }

        // Try to consume
        boolean success = inventoryRepo.consumeReservedOnOrder(productId, quantity);
        if (!success) {
            log.warn("Failed to consume reserved stock for product {} - skipping", productId);
            return null;
        }

        log.info("Successfully consumed {} reserved stock for product {}", quantity, productId);
        return getInventory(productId);
    }

    // ------------------------------------------------------------
    // HELPER METHODS
    // ------------------------------------------------------------
    private void ensureInventoryExists(Long productId) {
        if (inventoryRepo.findByProductId(productId).isEmpty()) {
            throw new ResourceNotFoundException("Inventory not found for product: " + productId);
        }
    }

    private InventoryResponse mapToResponse(Inventory inv) {
        InventoryResponse resp = new InventoryResponse();
        resp.setProductId(inv.getProductId());
        resp.setQuantity(inv.getQuantity());
        resp.setReserved(inv.getReserved());
        resp.setAvailable(inv.getAvailable());
        return resp;
    }
}