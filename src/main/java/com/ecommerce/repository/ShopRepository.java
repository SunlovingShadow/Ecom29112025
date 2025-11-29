package com.ecommerce.repository;

import com.ecommerce.model.Shop;
import java.util.List;
import java.util.Optional;

public interface ShopRepository {
    Long save(Shop shop);
    Shop update(Shop shop);
    Optional<Shop> getShopById(Long id);     // returns Optional
    List<Shop> getAllShops();
    List<Shop> getPendingApprovalShops();
    boolean approveShop(Long shopId);
    boolean rejectShop(Long shopId);
    boolean softDeleteShop(Long shopId);
    List<Shop> findByOwner(Long ownerUserId);
}