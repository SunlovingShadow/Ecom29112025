package com.ecommerce.repository.impl;

import com.ecommerce.model.Inventory;
import com.ecommerce.repository.InventoryRepository;
import com.ecommerce.repository.rowmapper.InventoryRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InventoryRepositoryImpl implements InventoryRepository {

	private final JdbcTemplate jdbcTemplate;

	public InventoryRepositoryImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

// ------------------------------------------------------------
// FIND BY PRODUCT
// ------------------------------------------------------------
	@Override
	public Optional<Inventory> findByProductId(Long productId) {
		String sql = "SELECT * FROM inventory WHERE product_id = ?";

		List<Inventory> list = jdbcTemplate.query(sql, new InventoryRowMapper(), productId);
		return list.stream().findFirst();
	}

// ------------------------------------------------------------
// CREATE INITIAL INVENTORY ROW
// ------------------------------------------------------------
	@Override
	public boolean createInventory(Long productId, int initialQuantity) {
		String sql = """
				INSERT INTO inventory (product_id, quantity, reserved)
				VALUES (?, ?, 0)
				""";

		return jdbcTemplate.update(sql, productId, initialQuantity) > 0;
	}

// ------------------------------------------------------------
// UPDATE FULL OBJECT
// ------------------------------------------------------------
	@Override
	public boolean update(Inventory inventory) {
		String sql = """
				UPDATE inventory
				SET quantity = ?, reserved = ?
				WHERE product_id = ?
				""";

		return jdbcTemplate.update(sql, inventory.getQuantity(), inventory.getReserved(), inventory.getProductId()) > 0;
	}

// ------------------------------------------------------------
// INCREASE STOCK (admin or stock arrival)
// ------------------------------------------------------------
	@Override
	public boolean increaseStock(Long productId, int quantity) {
		String sql = """
				UPDATE inventory
				SET quantity = quantity + ?
				WHERE product_id = ?
				""";

		return jdbcTemplate.update(sql, quantity, productId) > 0;
	}

// ------------------------------------------------------------
// DECREASE STOCK (manual)
// Ensure quantity does not go negative
// ------------------------------------------------------------
	@Override
	public boolean decreaseStock(Long productId, int quantity) {
		String sql = """
				UPDATE inventory
				SET quantity = quantity - ?
				WHERE product_id = ?
				AND quantity >= ?
				""";

		return jdbcTemplate.update(sql, quantity, productId, quantity) > 0;
	}

// ------------------------------------------------------------
// RESERVE STOCK FOR CHECKOUT
// available = quantity - reserved
// Condition: quantity - reserved >= requested
// ------------------------------------------------------------
	@Override
	public boolean reserveStock(Long productId, int quantity) {
		String sql = """
				UPDATE inventory
				SET reserved = reserved + ?
				WHERE product_id = ?
				AND (quantity - reserved) >= ?
				""";

		return jdbcTemplate.update(sql, quantity, productId, quantity) > 0;
	}

// ------------------------------------------------------------
// RELEASE RESERVED STOCK (cancel / timeout)
// ------------------------------------------------------------
	@Override
	public boolean releaseReservedStock(Long productId, int quantity) {
		String sql = """
				UPDATE inventory
				SET reserved = reserved - ?
				WHERE product_id = ?
				AND reserved >= ?
				""";

		return jdbcTemplate.update(sql, quantity, productId, quantity) > 0;
	}

// ------------------------------------------------------------
// CONSUME RESERVED ON ORDER CONFIRM
// quantity = quantity - reservedQty
// reserved = reserved - reservedQty
// ------------------------------------------------------------
	@Override
	public boolean consumeReservedOnOrder(Long productId, int quantity) {
		String sql = """
				UPDATE inventory
				SET
				quantity = quantity - ?,
				reserved = reserved - ?
				WHERE product_id = ?
				AND reserved >= ?
				AND quantity >= ?
				""";

		return jdbcTemplate.update(sql, quantity, // decrease quantity
				quantity, // decrease reserved
				productId, quantity, // ensure enough reserved
				quantity // ensure enough quantity
		) > 0;
	}
}