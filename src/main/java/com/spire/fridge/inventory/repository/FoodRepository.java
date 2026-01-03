package com.spire.fridge.inventory.repository;

import com.spire.fridge.inventory.entity.Category;
import com.spire.fridge.inventory.entity.Food;
import com.spire.fridge.inventory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByUser(User user);
    List<Food> findByUserAndCategory(User user, Category category);
}
