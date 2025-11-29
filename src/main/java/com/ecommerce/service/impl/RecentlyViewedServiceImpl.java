package com.ecommerce.service.impl;

import com.ecommerce.dto.RecentlyViewedResponse;
import com.ecommerce.model.RecentlyViewed;
import com.ecommerce.repository.RecentlyViewedRepository;
import com.ecommerce.service.RecentlyViewedService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecentlyViewedServiceImpl implements RecentlyViewedService {

    private final RecentlyViewedRepository recentlyViewedRepo;

    public RecentlyViewedServiceImpl(RecentlyViewedRepository recentlyViewedRepo) {
        this.recentlyViewedRepo = recentlyViewedRepo;
    }

    // ------------------------------------------------------------
    // ADD RECENTLY VIEWED PRODUCT
    // (Insert if not exists, else update timestamp)
    // ------------------------------------------------------------
    @Override
    public void addViewedProduct(Long userId, Long productId) {
        recentlyViewedRepo.saveOrUpdate(userId, productId);
    }

    // ------------------------------------------------------------
    // GET LAST N RECENTLY VIEWED PRODUCTS
    // ------------------------------------------------------------
    @Override
    public List<RecentlyViewedResponse> getRecentlyViewed(Long userId, int limit) {

        List<RecentlyViewed> list = recentlyViewedRepo.findLastNByUser(userId, limit);

        return list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------
    // CLEAR USER'S RECENTLY VIEWED HISTORY
    // ------------------------------------------------------------
    @Override
    public boolean clearRecentlyViewed(Long userId) {
        return recentlyViewedRepo.deleteAllByUser(userId);
    }

    // ------------------------------------------------------------
    // PRIVATE HELPER â€“ ENTITY TO RESPONSE DTO
    // ------------------------------------------------------------
    private RecentlyViewedResponse mapToResponse(RecentlyViewed rv) {

        RecentlyViewedResponse dto = new RecentlyViewedResponse();

        dto.setId(rv.getId());
        dto.setUserId(rv.getUserId());
        dto.setProductId(rv.getProductId());
        dto.setViewedAt(rv.getViewedAt());

        return dto;
    }
}