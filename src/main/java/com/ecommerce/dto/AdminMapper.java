package com.ecommerce.dto;

import com.ecommerce.model.*;

public class AdminMapper {

    public static UserResponse userToResponse(User u) {
        if (u == null) return null;
        UserResponse r = new UserResponse();
        r.setId(u.getId());
        r.setUsername(u.getUsername());
        r.setEmail(u.getEmail());
        r.setFullName(u.getFullName());
        r.setPhone(u.getPhone());
        r.setRole(u.getRole() == null ? null : u.getRole().name());
        r.setAccountStatus(u.getAccountStatus() == null ? null : u.getAccountStatus().name());
        r.setCreatedAt(u.getCreatedAt());
        return r;
    }
}