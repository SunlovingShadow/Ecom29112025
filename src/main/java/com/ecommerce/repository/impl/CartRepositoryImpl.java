package com.ecommerce.repository.impl;

import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.rowmapper.CartItemRowMapper;
import com.ecommerce.repository.rowmapper.CartRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class CartRepositoryImpl implements CartRepository {

    @Autowired
    private JdbcTemplate jdbc;

    private final CartRowMapper cartMapper = new CartRowMapper();
    private final CartItemRowMapper itemMapper = new CartItemRowMapper();

    @Override
    public Optional<Cart> findByUserId(Long userId) {
        String sql = "SELECT * FROM carts WHERE user_id = ?";
        try {
            Cart cart = jdbc.queryForObject(sql, cartMapper, userId);
            return Optional.ofNullable(cart);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Cart> findById(Long cartId) {
        String sql = "SELECT * FROM carts WHERE id = ?";
        try {
            Cart cart = jdbc.queryForObject(sql, cartMapper, cartId);
            return Optional.ofNullable(cart);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Cart create(Long userId) {
        String sql = "INSERT INTO carts (user_id, updated_at) VALUES (?, NOW())";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userId);
            return ps;
        }, keyHolder);

        Long newId = keyHolder.getKey().longValue();
        return findById(newId).orElseThrow();
    }

    @Override
    public void updateTimestamp(Long cartId) {
        String sql = "UPDATE carts SET updated_at = NOW() WHERE id = ?";
        jdbc.update(sql, cartId);
    }

    @Override
    public void delete(Long cartId) {
        clearCart(cartId); // Delete items first
        String sql = "DELETE FROM carts WHERE id = ?";
        jdbc.update(sql, cartId);
    }

    /* 
       ================================================================
       UPDATED METHOD FOR SOFT DELETE COMPLIANCE
       ================================================================
       We added 'AND p.is_active = TRUE' and 'AND s.is_active = TRUE'
       to the JOINs. 
       
       Effect: If a product or shop is 'Soft Deleted', it will 
       vanish from the user's cart view automatically.
    */
    @Override
    public List<CartItem> findItemsByCartId(Long cartId) {
        String sql = """
            SELECT ci.*, p.name as product_name, p.selling_price as current_price, 
                   s.id as shop_id, s.name as shop_name
            FROM cart_items ci
            JOIN products p ON ci.product_id = p.id AND p.is_active = TRUE
            JOIN shops s ON p.shop_id = s.id AND s.is_active = TRUE
            WHERE ci.cart_id = ?
        """;
        return jdbc.query(sql, itemMapper, cartId);
    }

    @Override
    public Optional<CartItem> findCartItem(Long cartId, Long productId) {
        String sql = "SELECT * FROM cart_items WHERE cart_id = ? AND product_id = ?";
        try {
            CartItem item = jdbc.queryForObject(sql, itemMapper, cartId, productId);
            return Optional.ofNullable(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public CartItem addItem(CartItem cartItem) {
        String sql = "INSERT INTO cart_items (cart_id, product_id, quantity, price_at_add) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, cartItem.getCartId());
            ps.setLong(2, cartItem.getProductId());
            ps.setInt(3, cartItem.getQuantity());
            ps.setBigDecimal(4, cartItem.getPriceAtAdd());
            return ps;
        }, keyHolder);

        cartItem.setId(keyHolder.getKey().longValue());
        updateTimestamp(cartItem.getCartId());
        return cartItem;
    }

    @Override
    public void updateItemQuantity(Long cartItemId, Integer quantity) {
        String sql = "UPDATE cart_items SET quantity = ? WHERE id = ?";
        jdbc.update(sql, quantity, cartItemId);
        
        // Update cart timestamp
        Optional<CartItem> item = findItemById(cartItemId);
        item.ifPresent(i -> updateTimestamp(i.getCartId()));
    }

    @Override
    public void removeItem(Long cartItemId) {
        Optional<CartItem> item = findItemById(cartItemId);
        String sql = "DELETE FROM cart_items WHERE id = ?";
        jdbc.update(sql, cartItemId);
        
        item.ifPresent(i -> updateTimestamp(i.getCartId()));
    }

    @Override
    public void clearCart(Long cartId) {
        String sql = "DELETE FROM cart_items WHERE cart_id = ?";
        jdbc.update(sql, cartId);
        updateTimestamp(cartId);
    }

    @Override
    public int getItemsCount(Long cartId) {
        String sql = "SELECT COUNT(*) FROM cart_items WHERE cart_id = ?";
        return jdbc.queryForObject(sql, Integer.class, cartId);
    }

    @Override
    public Optional<CartItem> findItemById(Long cartItemId) {
        String sql = "SELECT * FROM cart_items WHERE id = ?";
        try {
            CartItem item = jdbc.queryForObject(sql, itemMapper, cartItemId);
            return Optional.ofNullable(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
