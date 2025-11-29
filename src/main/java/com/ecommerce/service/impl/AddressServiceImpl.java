package com.ecommerce.service.impl;

import com.ecommerce.dto.AddressRequest;
import com.ecommerce.dto.AddressResponse;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.UnauthorizedException;

import com.ecommerce.model.Address;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.service.AddressService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepo;

    public AddressServiceImpl(AddressRepository addressRepo) {
        this.addressRepo = addressRepo;
    }

    // ------------------------------------------------------------
    // CREATE ADDRESS
    // ------------------------------------------------------------
    @Override
    @Transactional
    public AddressResponse createAddress(Long userId, AddressRequest req) {

        validateAddressRequest(req);

        Address newAddress = new Address();
        newAddress.setUserId(userId);
        newAddress.setFullName(req.getFullName());
        newAddress.setPhone(req.getPhone());
        newAddress.setPincode(req.getPincode());
        newAddress.setAddressLine1(req.getAddressLine1());
        newAddress.setAddressLine2(req.getAddressLine2());
        newAddress.setCity(req.getCity());
        newAddress.setState(req.getState());
        newAddress.setLandmark(req.getLandmark());
        newAddress.setAddressType(req.getAddressType());

        // If this is user's first address â†’ set default automatically
        int count = addressRepo.countByUser(userId);
        boolean isDefault = (count == 0) || Boolean.TRUE.equals(req.getIsDefault());

        newAddress.setIsDefault(isDefault);

        Long id = addressRepo.save(newAddress);
        newAddress.setId(id);

        // If user has chosen default = true â†’ unset all others and set new default
        if (Boolean.TRUE.equals(req.getIsDefault())) {
            addressRepo.unsetAllDefaults(userId);
            addressRepo.setDefault(userId, id);
        }

        return mapToResponse(newAddress);
    }

    // ------------------------------------------------------------
    // UPDATE ADDRESS
    // ------------------------------------------------------------
    @Override
    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest req) {

        Address existing = addressRepo.findByIdAndUser(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found!"));

        validateAddressRequest(req);

        existing.setFullName(req.getFullName());
        existing.setPhone(req.getPhone());
        existing.setPincode(req.getPincode());
        existing.setAddressLine1(req.getAddressLine1());
        existing.setAddressLine2(req.getAddressLine2());
        existing.setCity(req.getCity());
        existing.setState(req.getState());
        existing.setLandmark(req.getLandmark());
        existing.setAddressType(req.getAddressType());

        boolean requestedDefault = Boolean.TRUE.equals(req.getIsDefault());

        // If user wants to set this address as default
        if (requestedDefault) {
            addressRepo.unsetAllDefaults(userId);
            existing.setIsDefault(true);
        }

        addressRepo.update(existing);

        if (requestedDefault) {
            addressRepo.setDefault(userId, addressId);
        }

        return mapToResponse(existing);
    }

    // ------------------------------------------------------------
    // GET SINGLE ADDRESS
    // ------------------------------------------------------------
    @Override
    public AddressResponse getAddress(Long userId, Long addressId) {

        Address address = addressRepo.findByIdAndUser(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        return mapToResponse(address);
    }

    // ------------------------------------------------------------
    // GET ALL ADDRESSES OF USER
    // ------------------------------------------------------------
    @Override
    public List<AddressResponse> getAllAddresses(Long userId) {
        return addressRepo.findAllByUser(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------
    // DELETE ADDRESS (HARD DELETE)
    // ------------------------------------------------------------
    @Override
    @Transactional
    public boolean deleteAddress(Long userId, Long addressId) {

        Address address = addressRepo.findByIdAndUser(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        boolean wasDefault = Boolean.TRUE.equals(address.getIsDefault());

        boolean deleted = addressRepo.deleteById(addressId, userId);

        if (!deleted) {
            throw new BadRequestException("Unable to delete address");
        }

        // If deleted address was default â†’ set next active as default
        if (wasDefault) {
            List<Address> remaining = addressRepo.findAllByUser(userId);

            if (!remaining.isEmpty()) {
                Address first = remaining.get(0);
                addressRepo.setDefault(userId, first.getId());
            }
        }

        return true;
    }

    // ------------------------------------------------------------
    // SET DEFAULT ADDRESS
    // ------------------------------------------------------------
    @Override
    @Transactional
    public boolean setDefaultAddress(Long userId, Long addressId) {

        if (!addressRepo.existsByIdAndUser(addressId, userId)) {
            throw new ResourceNotFoundException("Address not found!");
        }

        addressRepo.unsetAllDefaults(userId);
        return addressRepo.setDefault(userId, addressId);
    }

    // ------------------------------------------------------------
    // PRIVATE HELPERS
    // ------------------------------------------------------------
    private void validateAddressRequest(AddressRequest req) {

        if (req.getFullName() == null || req.getFullName().isBlank()) {
            throw new BadRequestException("Full name is required");
        }
        if (req.getPhone() == null || req.getPhone().length() != 10) {
    	    throw new BadRequestException("Phone number must be exactly 10 digits");
	}

        if (req.getPincode() == null || req.getPincode().length() != 6) {
            throw new BadRequestException("Invalid pincode");
        }
        if (req.getAddressLine1() == null || req.getAddressLine1().isBlank()) {
            throw new BadRequestException("Address line 1 is required");
        }
        if (req.getCity() == null || req.getCity().isBlank()) {
            throw new BadRequestException("City is required");
        }
        if (req.getState() == null || req.getState().isBlank()) {
            throw new BadRequestException("State is required");
        }
        if (req.getAddressType() == null || req.getAddressType().isBlank()) {
            throw new BadRequestException("Address type is required (HOME/WORK)");
        }
    }

    private AddressResponse mapToResponse(Address address) {
        AddressResponse resp = new AddressResponse();

        resp.setId(address.getId());
        resp.setUserId(address.getUserId());
        resp.setFullName(address.getFullName());
        resp.setPhone(address.getPhone());
        resp.setPincode(address.getPincode());
        resp.setAddressLine1(address.getAddressLine1());
        resp.setAddressLine2(address.getAddressLine2());
        resp.setCity(address.getCity());
        resp.setState(address.getState());
        resp.setLandmark(address.getLandmark());
        resp.setAddressType(address.getAddressType());
        resp.setIsDefault(address.getIsDefault());
       // resp.setCreatedAt(address.getCreatedAt());

        return resp;
    }
}