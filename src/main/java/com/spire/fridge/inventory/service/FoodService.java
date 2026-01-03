package com.spire.fridge.inventory.security.services;

import com.spire.fridge.inventory.entity.Category;
import com.spire.fridge.inventory.entity.Food;
import com.spire.fridge.inventory.entity.User;
import com.spire.fridge.inventory.repository.CategoryRepository;
import com.spire.fridge.inventory.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FoodService {

    private final FoodRepository foodRepository;
    private final CategoryRepository categoryRepository;
    @Autowired
    public FoodService(FoodRepository foodRepository, CategoryRepository categoryRepository) {
        this.foodRepository = foodRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Food> getFoodByUser(User user) {
        return foodRepository.findByUser(user);
    }

    public List<Food> getFoodByUserAndCategory(User user, Category category) {
        return foodRepository.findByUserAndCategory(user, category);
    }


    public List<Food> saveAll(List<Food> foodList) {
        return foodRepository.saveAll(foodList);
    }

    public Optional<Food> GetById(Long id) {
        return foodRepository.findById(id);
    }

    public void delete(Long foodId) {
        foodRepository.deleteById(foodId);
    }

}