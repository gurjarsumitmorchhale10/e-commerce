package com.luv2code.inventory.repository;

import com.luv2code.inventory.entity.InventoryBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface InventoryRepository extends JpaRepository<InventoryBalance, Long> {
    Optional<InventoryBalance> findBySkuCode(String skuCode);

    Set<InventoryBalance> findBySkuCodeIn(List<String> skuCodes);
}