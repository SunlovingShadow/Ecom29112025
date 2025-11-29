package com.ecommerce.service;

import com.ecommerce.dto.ShopRequest;
import com.ecommerce.dto.ShopResponse;

import java.util.List;

public interface ShopService {
    ShopResponse createShop(ShopRequest req);
    ShopResponse updateShop(Long id, ShopRequest req);
    ShopResponse getShopById(Long id);
    List<ShopResponse> getAllShops();
    List<ShopResponse> getPendingApprovalShops();
    boolean approveShop(Long shopId);
    boolean rejectShop(Long shopId);
    boolean softDeleteShop(Long shopId);
}