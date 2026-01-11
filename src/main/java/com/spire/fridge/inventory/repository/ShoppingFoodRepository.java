package com.spire.fridge.inventory.repository;

import com.spire.fridge.inventory.classification.LifeCategory;
import com.spire.fridge.inventory.entity.Category;
import com.spire.fridge.inventory.entity.ShoppingFood;
import com.spire.fridge.inventory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoppingFoodRepository extends JpaRepository<ShoppingFood, Long> {
    List<ShoppingFood> findByUser(User user);
    List<ShoppingFood> findByUserAndCategory(User user, Category category);
    List<ShoppingFood> findByUserAndLifeCategory(User user, LifeCategory lifeCategory);
}
