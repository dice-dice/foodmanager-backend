package com.spire.fridge.inventory.dto.stats;

import java.util.List;

public class OverviewStatsDTO {
    private long totalFoods;
    private long expiringIn3Days;
    private long expiredCount;
    private List<CategoryBreakdownDTO> categoryBreakdown;
    private long shoppingListCount;

    public OverviewStatsDTO() {}

    public OverviewStatsDTO(long totalFoods, long expiringIn3Days, long expiredCount,
                            List<CategoryBreakdownDTO> categoryBreakdown, long shoppingListCount) {
        this.totalFoods = totalFoods;
        this.expiringIn3Days = expiringIn3Days;
        this.expiredCount = expiredCount;
        this.categoryBreakdown = categoryBreakdown;
        this.shoppingListCount = shoppingListCount;
    }

    public long getTotalFoods() {
        return totalFoods;
    }

    public void setTotalFoods(long totalFoods) {
        this.totalFoods = totalFoods;
    }

    public long getExpiringIn3Days() {
        return expiringIn3Days;
    }

    public void setExpiringIn3Days(long expiringIn3Days) {
        this.expiringIn3Days = expiringIn3Days;
    }

    public long getExpiredCount() {
        return expiredCount;
    }

    public void setExpiredCount(long expiredCount) {
        this.expiredCount = expiredCount;
    }

    public List<CategoryBreakdownDTO> getCategoryBreakdown() {
        return categoryBreakdown;
    }

    public void setCategoryBreakdown(List<CategoryBreakdownDTO> categoryBreakdown) {
        this.categoryBreakdown = categoryBreakdown;
    }

    public long getShoppingListCount() {
        return shoppingListCount;
    }

    public void setShoppingListCount(long shoppingListCount) {
        this.shoppingListCount = shoppingListCount;
    }
}
