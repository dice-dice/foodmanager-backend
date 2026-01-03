package com.spire.fridge.inventory.security.services;

import com.spire.fridge.inventory.entity.Category;
import com.spire.fridge.inventory.entity.Food;
import com.spire.fridge.inventory.entity.ShoppingFood;
import com.spire.fridge.inventory.entity.User;
import com.spire.fridge.inventory.repository.CategoryRepository;
import com.spire.fridge.inventory.repository.ShoppingFoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShoppingFoodService {
    private final ShoppingFoodRepository shoppingFoodRepository;

    private final CategoryRepository categoryRepository;


    @Autowired
    public ShoppingFoodService(ShoppingFoodRepository shoppingFoodRepository, CategoryRepository categoryRepository) {
        this.shoppingFoodRepository = shoppingFoodRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<ShoppingFood> getShoppingFoodByUserAndCategory(User user, Category category) {
        return shoppingFoodRepository.findByUserAndCategory(user, category);
    }
    public List<ShoppingFood> getShoppingFoodByUser(User user) {
        return shoppingFoodRepository.findByUser(user);
    }

    public List<ShoppingFood> saveAll(List<ShoppingFood> shoppingFoodList) {
        return shoppingFoodRepository.saveAll(shoppingFoodList);
    }

    public Optional<ShoppingFood> GetById(Long id) {
        return shoppingFoodRepository.findById(id);
    }

    public void delete(Long shoppingFoodId) {
        shoppingFoodRepository.deleteById(shoppingFoodId);
    }
}
