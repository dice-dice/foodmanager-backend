package com.spire.fridge.inventory.entity;

import com.spire.fridge.inventory.classification.LifeCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "shopping_foods")
public class ShoppingFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 20)
    private String name;

    @Min(value = 0, message = "Quantity must be greater than equal to 0")
    @Max(value = 1000, message = "Quantity must be less than or equal to 1000")
    private Integer quantity;


    private LocalDate date;

    public LifeCategory getLifeCategory() {
        return lifeCategory;
    }

    public void setLifeCategory(LifeCategory lifeCategory) {
        this.lifeCategory = lifeCategory;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "life_category", length = 20)
    private LifeCategory lifeCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;


    public ShoppingFood() {

    }

    public ShoppingFood(String name, Integer quantity, LocalDate date, User user) {
        this.name = name;
        this.quantity = quantity;
        this.date = date;
        this.user = user;

    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id; }

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDate getDate() {
        return date;
    }

    @PrePersist
    public void PrePersist() {
        if(date == null) {
            date =LocalDate.now();
        }
    }

    public void setDate(LocalDate date) {
        this.date = date != null ? date : LocalDate.now();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
