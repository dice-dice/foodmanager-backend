package com.spire.fridge.inventory.dto.stats;

public class CategoryBreakdownDTO {
    private Long categoryId;
    private String category;
    private long count;

    public CategoryBreakdownDTO() {}

    public CategoryBreakdownDTO(Long categoryId, String category, long count) {
        this.categoryId = categoryId;
        this.category = category;
        this.count = count;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
