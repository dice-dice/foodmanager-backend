package com.spire.fridge.inventory.repository;

import com.spire.fridge.inventory.classification.LifeCategory;
import com.spire.fridge.inventory.entity.Category;
import com.spire.fridge.inventory.entity.ShoppingFood;
import com.spire.fridge.inventory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingFoodRepository extends JpaRepository<ShoppingFood, Long> {
    List<ShoppingFood> findByUser(User user);
    List<ShoppingFood> findByUserAndCategory(User user, Category category);
    List<ShoppingFood> findByUserAndLifeCategory(User user, LifeCategory lifeCategory);

    // 統計用クエリメソッド
    long countByUser(User user);
}
