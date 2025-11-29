package com.ecommerce.service.impl;

import com.ecommerce.dto.*;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.*;
import com.ecommerce.repository.*;
import com.ecommerce.service.ProductVariantService;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductVariantServiceImpl implements ProductVariantService {

private final ProductRepository productRepository;
private final ProductVariantGroupRepository groupRepository;
private final ProductVariantValueRepository valueRepository;
private final ProductVariantStockRepository stockRepository;

public ProductVariantServiceImpl(ProductRepository productRepository,
ProductVariantGroupRepository groupRepository,
ProductVariantValueRepository valueRepository,
ProductVariantStockRepository stockRepository) {
this.productRepository = productRepository;
this.groupRepository = groupRepository;
this.valueRepository = valueRepository;
this.stockRepository = stockRepository;
}

// ---------------- helpers ----------------

private Product assertProductActive(Long productId) {
Product p = productRepository.findById(productId)
.orElseThrow(() -> new ResourceNotFoundException("Product not found"));
if (Boolean.FALSE.equals(p.getIsActive())) {
throw new BadRequestException("Product is inactive");
}
return p;
}

private ProductVariantGroup assertGroupExists(Long groupId) {
return groupRepository.findById(groupId)
.orElseThrow(() -> new ResourceNotFoundException("Variant group not found"));
}

private ProductVariantValue assertValueExists(Long valueId) {
return valueRepository.findById(valueId)
.orElseThrow(() -> new ResourceNotFoundException("Variant value not found"));
}

// ================= GROUPS =================

@Override
public ProductVariantGroupResponse createGroup(Long productId, ProductVariantGroupRequest request) {
assertProductActive(productId);

if (request.getGroupName() == null || request.getGroupName().isBlank()) {
throw new BadRequestException("Variant group name is required");
}

ProductVariantGroup g = new ProductVariantGroup();
g.setProductId(productId);
g.setGroupName(request.getGroupName());

Long id = groupRepository.save(g);
g.setId(id);

return mapGroup(g);
}

@Override
public List<ProductVariantGroupResponse> getGroupsByProduct(Long productId) {
assertProductActive(productId);
return groupRepository.findByProductId(productId)
.stream().map(this::mapGroup).collect(Collectors.toList());
}

@Override
public ProductVariantGroupResponse updateGroup(Long groupId, ProductVariantGroupRequest request) {
ProductVariantGroup g = assertGroupExists(groupId);

if (request.getGroupName() != null && !request.getGroupName().isBlank()) {
g.setGroupName(request.getGroupName());
}

groupRepository.update(g);
return mapGroup(g);
}

@Override
public boolean deleteGroup(Long groupId) {
// additionally you *could* delete its values & stock, but abhi simple:
assertGroupExists(groupId);
return groupRepository.delete(groupId);
}

// ================= VALUES =================

@Override
public ProductVariantValueResponse createValue(Long groupId, ProductVariantValueRequest request) {
ProductVariantGroup g = assertGroupExists(groupId);

if (request.getValueName() == null || request.getValueName().isBlank()) {
throw new BadRequestException("Variant value is required");
}

ProductVariantValue v = new ProductVariantValue();
v.setGroupId(g.getId());
v.setValueName(request.getValueName());

Long id = valueRepository.save(v);
v.setId(id);

return mapValue(v);
}

@Override
public List<ProductVariantValueResponse> getValuesByGroup(Long groupId) {
assertGroupExists(groupId);
return valueRepository.findByGroupId(groupId)
.stream().map(this::mapValue).collect(Collectors.toList());
}

@Override
public ProductVariantValueResponse updateValue(Long valueId, ProductVariantValueRequest request) {
ProductVariantValue v = assertValueExists(valueId);

if (request.getValueName() != null && !request.getValueName().isBlank()) {
v.setValueName(request.getValueName());
}

valueRepository.update(v);
return mapValue(v);
}

@Override
public boolean deleteValue(Long valueId) {
assertValueExists(valueId);
return valueRepository.delete(valueId);
}

// ================= STOCK =================

@Override
public ProductVariantStockResponse upsertStock(Long productId, Long variantValueId,
ProductVariantStockRequest request) {

assertProductActive(productId);
assertValueExists(variantValueId);

if (request.getQuantity() == null) {
throw new BadRequestException("Stock quantity is required");
}

ProductVariantStock existing = stockRepository
.findByProductAndValue(productId, variantValueId)
.orElse(null);

if (existing == null) {
existing = new ProductVariantStock();
existing.setProductId(productId);
existing.setVariantValueId(variantValueId);
}

existing.setQuantity(request.getQuantity());
existing.setPriceOffset(request.getPriceOffset() != null
? request.getPriceOffset()
: BigDecimal.ZERO);

if (existing.getId() == null) {
Long id = stockRepository.save(existing);
existing.setId(id);
} else {
stockRepository.update(existing);
}

return mapStock(existing);
}

@Override
public List<ProductVariantStockResponse> getStockByProduct(Long productId) {
assertProductActive(productId);
return stockRepository.findByProductId(productId)
.stream().map(this::mapStock).collect(Collectors.toList());
}

// -------------- mappers -----------------

private ProductVariantGroupResponse mapGroup(ProductVariantGroup g) {
ProductVariantGroupResponse r = new ProductVariantGroupResponse();
r.setId(g.getId());
r.setProductId(g.getProductId());
r.setGroupName(g.getGroupName());
return r;
}

private ProductVariantValueResponse mapValue(ProductVariantValue v) {
ProductVariantValueResponse r = new ProductVariantValueResponse();
r.setId(v.getId());
r.setGroupId(v.getGroupId());
r.setValueName(v.getValueName());
return r;
}

private ProductVariantStockResponse mapStock(ProductVariantStock s) {
ProductVariantStockResponse r = new ProductVariantStockResponse();
r.setId(s.getId());
r.setProductId(s.getProductId());
r.setVariantValueId(s.getVariantValueId());
r.setQuantity(s.getQuantity());
r.setPriceOffset(s.getPriceOffset());
return r;
}
}