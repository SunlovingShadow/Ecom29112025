package com.ecommerce.service.impl;

import com.ecommerce.dto.ShopRequest;
import com.ecommerce.dto.ShopResponse;
import com.ecommerce.enums.UserRole;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Shop;
import com.ecommerce.model.User;
import com.ecommerce.repository.ShopRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.ShopService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
	private ShopRepository shopRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public ShopResponse createShop(ShopRequest req) {
        
    	User owner=userRepository.findById(req.getOwnerUserId())
    			.orElseThrow(()->new ResourceNotFoundException("User not found with ID"+req.getOwnerUserId()));
    	
    	if(owner.getRole()!=UserRole.SHOPKEEPER) {
    		throw new BadRequestException("Only SHOPKEEPERS can create a shop");
    	}
    	
    	if(req.getName()==null || req.getName().trim().isEmpty())
    		throw new BadRequestException("Shop name cannot be empty");
    	
    	Shop s = new Shop();
        s.setOwnerUserId(req.getOwnerUserId());
        s.setName(req.getName());
        s.setDescription(req.getDescription());
        s.setAddress(req.getAddress());
        s.setIsApproved(false);
        s.setIsActive(true);

        Long id = shopRepository.save(s);
        s.setId(id);
        return DtoMapper.shopToResponse(s);
    }

    @Override
    public ShopResponse updateShop(Long id, ShopRequest req) {
        Shop s = shopRepository.getShopById(id).orElseThrow(() -> new RuntimeException("Shop not found"));
        s.setName(req.getName());
        s.setDescription(req.getDescription());
        s.setAddress(req.getAddress());
        Shop out = shopRepository.update(s);
        return DtoMapper.shopToResponse(out);
    }

    @Override
    public ShopResponse getShopById(Long id) {
        return shopRepository.getShopById(id).map(DtoMapper::shopToResponse)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
    }

    @Override
    public List<ShopResponse> getAllShops() {
        return shopRepository.getAllShops().stream().map(DtoMapper::shopToResponse).collect(Collectors.toList());
    }

    @Override
    public List<ShopResponse> getPendingApprovalShops() {
        return shopRepository.getPendingApprovalShops().stream().map(DtoMapper::shopToResponse).collect(Collectors.toList());
    }

    @Override
    public boolean approveShop(Long shopId) {
        return shopRepository.approveShop(shopId);
    }

    @Override
    public boolean rejectShop(Long shopId) {
        return shopRepository.rejectShop(shopId);
    }

    @Override
    public boolean softDeleteShop(Long shopId) {
        return shopRepository.softDeleteShop(shopId);
    }
}