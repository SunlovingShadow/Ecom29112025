package com.ecommerce.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.dto.AdminMapper;
import com.ecommerce.dto.CouponRequest;
import com.ecommerce.dto.CouponResponse;
import com.ecommerce.dto.ShopResponse;
import com.ecommerce.dto.UserResponse;
import com.ecommerce.enums.AccountStatus;
import com.ecommerce.enums.DiscountType;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.AdminLog;
import com.ecommerce.model.Coupon;
import com.ecommerce.model.Shop;
import com.ecommerce.model.User;
import com.ecommerce.repository.AdminLogsRepository;
import com.ecommerce.repository.CouponRepository;
import com.ecommerce.repository.ShopRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.dto.AdminLogResponse;
import com.ecommerce.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	private  UserRepository userRepository;
	
	@Autowired
    private  ShopRepository shopRepository;
	
	@Autowired
    private  CouponRepository couponRepository;
	
	@Autowired
    private  AdminLogsRepository adminLogsRepository;
	
	@Override
	public void logAction(Long adminUserId, String action) {
		try {
            AdminLog log = new AdminLog();
            log.setAdminUserId(adminUserId);
            log.setAction(action);
            log.setCreatedAt(LocalDateTime.now());
            adminLogsRepository.save(log);
        } catch (Exception ex) {
            // don't fail admin operations if logging fails
        }
	}
	
	@Override
	public List<UserResponse> getAllUsers(Long adminUserId) {
		return userRepository.findAll()
                .stream()
                .map(AdminMapper::userToResponse)
                .collect(Collectors.toList());
	}

	@Override
	public UserResponse getUserById(Long adminUserId,Long userId) {
		User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return AdminMapper.userToResponse(user);
	}

	@Override
	public boolean updateUserStatus(Long adminUserId, Long userId, String status) {
		 User user=userRepository.findById(userId)
				 .orElseThrow(() -> new ResourceNotFoundException("User not found"));
		 user.setAccountStatus(AccountStatus.valueOf(status));
		 
		 boolean updated=userRepository.update(user);
		 logAction(adminUserId,"UPDATE_USER_STATUS:userId="+userId+"status="+status);
		 return updated;
	}

	@Override
	public void deleteUser(Long adminUserId, Long userId) {
		User user=userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		  userRepository.softDelete(userId);		
		
		  logAction(adminUserId,"DELETE_USER:userId="+userId);
	}

	@Override
	public List<ShopResponse> getAllShops(Long adminUserId) {
		logAction(adminUserId,"VIEW_ALL_SHOPS");
		return shopRepository.getAllShops().stream().map(DtoMapper::shopToResponse).collect(Collectors.toList());
	}
	
	@Override
	public List<ShopResponse> getPendingShops(Long adminUserId) {
		logAction(adminUserId,"VIEW_PENDING_SHOPS");
		return shopRepository.getPendingApprovalShops().stream().map(DtoMapper::shopToResponse).collect(Collectors.toList());
	}
	

	@Override
	public ShopResponse getShopById(Long adminUserId,Long shopId) {
		Shop shop = shopRepository.getShopById(shopId).orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        return DtoMapper.shopToResponse(shop);
	}

	@Override
	public boolean approveShop(Long adminUserId, Long shopId) {
		boolean ok = shopRepository.approveShop(shopId);
        if (!ok) throw new BadRequestException("Failed to approve");
        logAction(adminUserId, "APPROVE_SHOP: shopId=" + shopId);
        return true;
	}

	@Override
	public boolean rejectShop(Long adminUserId, Long shopId) {
		 boolean ok = shopRepository.rejectShop(shopId);
	        if (!ok) throw new BadRequestException("Failed to reject");
	        logAction(adminUserId, "REJECT_SHOP: shopId=" + shopId);
	        return true;
	}

	@Override
	public boolean softDeleteShop(Long adminUserId, Long shopId) {
		 if (shopRepository.getShopById(shopId).isEmpty()) throw new ResourceNotFoundException("Shop not found");
	        boolean ok = shopRepository.softDeleteShop(shopId);
	        if (ok) logAction(adminUserId, "SOFT_DELETE_SHOP: shopId=" + shopId);
	        return ok;
	}

	@Override
	public List<CouponResponse> getAllCoupons(Long adminUserId) {
		logAction(adminUserId,"ADMIN_VIEW_COUPONS");
		return couponRepository.findAll().stream().map(DtoMapper::couponToResponse).collect(Collectors.toList());
	}

	@Override
	public CouponResponse createCoupon(Long adminUserId, CouponRequest request) {
	        Coupon c = new Coupon();
	        c.setCode(request.getCode());
	        c.setDiscountType(DiscountType.valueOf(request.getDiscountType())); // assume service will convert to enum later if needed
	        c.setDiscountValue(request.getDiscountValue());
	        c.setMinOrderAmount(request.getMinOrderAmount());
	        // DTO has Dates; service / repo convert as previously discussed
	        c.setValidFrom(LocalDate.parse(request.getValidFrom())); 
	        c.setValidTo(LocalDate.parse(request.getValidTo())); // set in repo impl or convert here (depends where you stored)
	        c.setShopId(request.getShopId());
	        c.setActive(true);
	        couponRepository.save(c);

	        logAction(adminUserId, "CREATE_COUPON: code=" + request.getCode());
	        return DtoMapper.couponToResponse(c);
	}

	@Override
	public CouponResponse updateCoupon(Long adminUserId, Long couponId, CouponRequest request) {
		Coupon existing = couponRepository.findById(couponId).orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        if (existing == null) throw new ResourceNotFoundException("Coupon not found");
        
        existing.setCode(request.getCode());
        existing.setDiscountType(DiscountType.valueOf(request.getDiscountType()));
        existing.setDiscountValue(request.getDiscountValue());
        existing.setMinOrderAmount(request.getMinOrderAmount());
        existing.setShopId(request.getShopId());
        couponRepository.update(existing);

        logAction(adminUserId, "UPDATE_COUPON: couponId=" + couponId);
        return DtoMapper.couponToResponse(existing);
	}

	@Override
	public boolean deleteCoupon(Long adminUserId, Long couponId) {
		boolean ok=couponRepository.softDelete(couponId);
		if(!ok) throw new RuntimeException("Delete coupon failed");
		logAction(adminUserId,"ADMIN_SOFT_DELETE_COUPON id="+couponId);
		return true;
	}

	@Override
	public List<AdminLogResponse> getRecentAdminLogs(int limit) {
		 return adminLogsRepository.findRecent(limit)
	                .stream()
	                .map(l -> {
	                    AdminLogResponse r = new AdminLogResponse();
	                    r.setId(l.getId());
	                    r.setAdminUserId(l.getAdminUserId());
	                    r.setAction(l.getAction());
	                    r.setCreatedAt(l.getCreatedAt());
	                    return r;
	                }).collect(Collectors.toList());
	}
}