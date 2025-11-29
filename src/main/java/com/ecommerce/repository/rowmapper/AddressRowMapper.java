package com.ecommerce.repository.rowmapper;

import com.ecommerce.model.Address;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AddressRowMapper implements RowMapper<Address> {

    @Override
    public Address mapRow(ResultSet rs, int rowNum) throws SQLException {

        Address address = new Address();

        address.setId(rs.getLong("id"));
        address.setUserId(rs.getLong("user_id"));

        address.setFullName(rs.getString("full_name"));
        address.setPhone(rs.getString("phone"));
        address.setPincode(rs.getString("pincode"));

        address.setAddressLine1(rs.getString("address_line1"));
        address.setAddressLine2(rs.getString("address_line2"));

        address.setCity(rs.getString("city"));
        address.setState(rs.getString("state"));
        address.setLandmark(rs.getString("landmark"));

        address.setAddressType(rs.getString("address_type"));
        address.setIsDefault(rs.getBoolean("is_default"));

       
        return address;
    }
}