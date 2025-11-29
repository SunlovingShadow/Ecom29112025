package com.ecommerce.service;

import com.ecommerce.dto.AddressRequest;
import com.ecommerce.dto.AddressResponse;

import java.util.List;

public interface AddressService {

    // CREATE new address
    AddressResponse createAddress(Long userId, AddressRequest request);

    // UPDATE existing address
    AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request);

    // GET SINGLE address (only if owned by user)
    AddressResponse getAddress(Long userId, Long addressId);

    // GET ALL addresses of user
    List<AddressResponse> getAllAddresses(Long userId);

    // HARD DELETE (permanent removal)
    boolean deleteAddress(Long userId, Long addressId);

    // SET default address for user (unset others)
    boolean setDefaultAddress(Long userId, Long addressId);
}