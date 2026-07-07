package com.esunbank.library.common.dto;

import jakarta.validation.constraints.NotNull;

public class BorrowRequest {

    @NotNull(message = "inventoryId 不可為空")
    private Long inventoryId;

    public Long getInventoryId() { return inventoryId; }
    public void setInventoryId(Long inventoryId) { this.inventoryId = inventoryId; }
}