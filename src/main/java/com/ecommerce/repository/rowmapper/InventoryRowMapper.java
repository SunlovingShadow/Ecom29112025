package com.ecommerce.repository.rowmapper;

import com.ecommerce.model.Inventory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryRowMapper implements RowMapper<Inventory> {

	@Override
	public Inventory mapRow(ResultSet rs, int rowNum) throws SQLException {

		Inventory inv = new Inventory();
		inv.setProductId(rs.getLong("product_id"));
		inv.setQuantity(rs.getInt("quantity"));
		inv.setReserved(rs.getInt("reserved"));

		return inv;
	}
}