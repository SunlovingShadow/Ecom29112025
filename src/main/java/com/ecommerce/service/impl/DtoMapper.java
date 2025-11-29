
      package com.ecommerce.service.impl;

import com.ecommerce.dto.*;
import com.ecommerce.model.*;

public class DtoMapper {

    /*------------------------------------------------------
     *               SHOP MAPPING
     *------------------------------------------------------*/
    public static ShopResponse shopToResponse(Shop s) {
        if (s == null) return null;

        ShopResponse r = new ShopResponse();
        r.setId(s.getId());
        r.setOwnerUserId(s.getOwnerUserId());
        r.setName(s.getName());
        r.setDescription(s.getDescription());
        r.setAddress(s.getAddress());
        r.setIsApproved(s.getIsApproved());
        r.setIsActive(s.getIsActive());
        r.setCreatedAt(s.getCreatedAt());

        return r;
    }

    /*------------------------------------------------------
     *               COUPON MAPPING
     *------------------------------------------------------*/
    public static CouponResponse couponToResponse(Coupon c) {
        if (c == null) return null;

        CouponResponse r = new CouponResponse();
        r.setId(c.getId());
        r.setCode(c.getCode());
        
        // Handle Enum safely
        if (c.getDiscountType() != null) {
            r.setDiscountType(c.getDiscountType().name());
        }

        // *** FIX: Convert BigDecimal to double ***
        if (c.getDiscountValue() != null) {
            r.setDiscountValue(c.getDiscountValue().doubleValue());
        } else {
            r.setDiscountValue(0.0);
        }

        if (c.getMinOrderAmount() != null) {
            r.setMinOrderAmount(c.getMinOrderAmount().doubleValue());
        } else {
            r.setMinOrderAmount(0.0);
        }

        r.setValidFrom(c.getValidFrom());
        r.setValidTo(c.getValidTo());
        r.setActive(c.isActive());
        r.setShopId(c.getShopId());

        return r;
    }

    /*------------------------------------------------------
     *               ADMIN LOG MAPPING
     *------------------------------------------------------*/
    public static AdminLogResponse adminLogToResponse(AdminLog a) {
        if (a == null) return null;

        AdminLogResponse r = new AdminLogResponse();
        r.setId(a.getId());
        r.setAdminUserId(a.getAdminUserId());
        r.setAction(a.getAction());
        r.setCreatedAt(a.getCreatedAt());

        return r;
    }
}
        