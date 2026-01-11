package com.spire.fridge.inventory.controller;

import com.spire.fridge.inventory.dto.food.FoodDTO;
import com.spire.fridge.inventory.entity.Category;
import com.spire.fridge.inventory.entity.Food;
import com.spire.fridge.inventory.entity.User;
import com.spire.fridge.inventory.repository.CategoryRepository;
import com.spire.fridge.inventory.repository.UserRepository;
import com.spire.fridge.inventory.service.FoodService;
import com.spire.fridge.inventory.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//@CrossOrigin(origins = "https://food-manager.jp", maxAge = 3600,  allowCredentials = "true")
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:8080"}, maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/food")
public class FoodController {
    private final FoodService foodService;
    private final UserRepository userRepository;

    private CategoryRepository categoryRepository;

    @Autowired
    public FoodController(FoodService foodService, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.foodService = foodService;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public FoodDTO mapToDTO(Food food) {
        FoodDTO foodDTO = new FoodDTO();
        foodDTO.setId(food.getId());
        foodDTO.setName(food.getName());
        foodDTO.setQuantity(food.getQuantity());
        foodDTO.setDate(food.getDate());
        Category category = food.getCategory();
        if (category != null) {
            foodDTO.setCategoryId(category.getId());
            foodDTO.setCategoryName(category.getName());
        }

        return foodDTO;
    }

    @GetMapping("/by-user")
    public ResponseEntity<List<FoodDTO>> getFoodByUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);

        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Food> foodList = foodService.getFoodByUser(currentUser);
        List<FoodDTO> foodDTOList = foodList.stream().map(food -> mapToDTO(food)).collect(Collectors.toList());
        return new ResponseEntity<>(foodDTOList, HttpStatus.OK);
    }

    @GetMapping("/by-category/{userId}/{categoryId}")
    public ResponseEntity<List<FoodDTO>> getFoodByCategory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long userId,
            @PathVariable Long categoryId
    ) {
        User user = userRepository.findById(userId).orElse(null);
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if(user == null || category == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Food> foodList = foodService.getFoodByUserAndCategory(user, category);
        List<FoodDTO> foodDTOList = foodList.stream().map(this::mapToDTO).collect(Collectors.toList());
        return new ResponseEntity<>(foodDTOList,HttpStatus.OK);
    }

    @PostMapping("/food-stock")
    public ResponseEntity<List<Food>> foodStock(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody List<FoodDTO> foodDTOList
    ) {
        System.out.println("Request Body: " + foodDTOList); // デバッグログの出力
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);

        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Food> foodList = new ArrayList<>();
        for (FoodDTO foodDTO : foodDTOList) {
            Food food = new Food();
            food.setName(foodDTO.getName());
            food.setQuantity(foodDTO.getQuantity());
            food.setDate(foodDTO.getDate());
            food.setUser(currentUser);

            Long categoryId = foodDTO.getCategoryId();
            if (categoryId != null) {
                Category storedCategory = categoryRepository.findById(categoryId).orElse(null);
                if (storedCategory == null) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                food.setCategory(storedCategory);
            }

            foodList.add(food);
        }

        List<Food> savedFoodList = foodService.saveAll(foodList);

        return new ResponseEntity<>(savedFoodList, HttpStatus.OK);
    }


    @PutMapping("/food-update")
    public ResponseEntity<List<FoodDTO>> updatedFoodStock(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody List<FoodDTO> foodDTOList
    ) {
        System.out.println("Request Body: " + foodDTOList); // デバッグログの出力
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);

        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


        List<Food> updateFoodList = new ArrayList<>();
        for (FoodDTO foodDTO : foodDTOList) {
            Long foodId = foodDTO.getId();
            if (foodId != null) {
                Optional<Food> optionalFood = foodService.GetById(foodId);
                if (optionalFood.isPresent()) {
                    Food existingFood = optionalFood.get();
                    existingFood.setName(foodDTO.getName());
                    existingFood.setQuantity(foodDTO.getQuantity());
                    existingFood.setDate(foodDTO.getDate());


                    Long categoryId = foodDTO.getCategoryId();
                    if (categoryId != null) {
                        Category storedCategory = categoryRepository.findById(categoryId).orElse(null);
                        if (storedCategory != null) {
                            existingFood.setCategory(storedCategory);
                        } else {
                            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                        }
                    }
                    updateFoodList.add(existingFood);
                }
            }
        }
        List<Food> savedFoodList = foodService.saveAll(updateFoodList);

        List<FoodDTO> savedFoodDTOList = savedFoodList.stream().map(this::mapToDTO).collect(Collectors.toList());

        return new ResponseEntity<>(savedFoodDTOList, HttpStatus.OK);
    }

    @DeleteMapping("/food-delete/{id}")
    public ResponseEntity<String> foodDelete(@PathVariable("id") Long id,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);

        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<Food> optionalFood = foodService.GetById(id);

        if (optionalFood.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Food food = optionalFood.get();
        if (!food.getUser().equals(currentUser)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        foodService.delete(id);

        return new ResponseEntity<>("Food deleted successfully!", HttpStatus.OK);

    }
}
