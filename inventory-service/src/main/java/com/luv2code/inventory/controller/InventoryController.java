package com.luv2code.inventory.controller;

import com.luv2code.inventory.dto.InventoryDto;
import com.luv2code.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/add-stock")
    void addStock(@RequestBody InventoryDto inventoryDto) {
        inventoryService.addStock(inventoryDto);
    }

}
