package com.spire.fridge.inventory.dto.stats;

public class ExpirationItemDTO {
    private Long id;
    private String name;
    private int daysLeft;
    private String categoryName;

    public ExpirationItemDTO() {}

    public ExpirationItemDTO(Long id, String name, int daysLeft, String categoryName) {
        this.id = id;
        this.name = name;
        this.daysLeft = daysLeft;
        this.categoryName = categoryName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDaysLeft() {
        return daysLeft;
    }

    public void setDaysLeft(int daysLeft) {
        this.daysLeft = daysLeft;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
