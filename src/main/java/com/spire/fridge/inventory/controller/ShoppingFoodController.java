package com.spire.fridge.inventory.controller;

import com.spire.fridge.inventory.dto.food.FoodDTO;
import com.spire.fridge.inventory.entity.Category;
import com.spire.fridge.inventory.entity.Food;
import com.spire.fridge.inventory.entity.ShoppingFood;
import com.spire.fridge.inventory.entity.User;
import com.spire.fridge.inventory.repository.CategoryRepository;
import com.spire.fridge.inventory.repository.UserRepository;
import com.spire.fridge.inventory.security.services.ShoppingFoodService;
import com.spire.fridge.inventory.security.services.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//@CrossOrigin(origins = "https://food-manager.jp", maxAge = 3600, allowCredentials = "true")
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:8080"}, maxAge = 3600, allowCredentials = "true")
@Controller
@RequestMapping("api/shopping")
public class ShoppingFoodController {

    private final ShoppingFoodService shoppingFoodService;
    private final UserRepository userRepository;
    private CategoryRepository categoryRepository;

    public ShoppingFoodController(ShoppingFoodService shoppingFoodService, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.shoppingFoodService = shoppingFoodService;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public FoodDTO mapToDTO(ShoppingFood shoppingFood) {
        FoodDTO foodDTO = new FoodDTO();
        foodDTO.setId(shoppingFood.getId());
        foodDTO.setName(shoppingFood.getName());
        foodDTO.setQuantity(shoppingFood.getQuantity());
        foodDTO.setDate(shoppingFood.getDate());
        Category category = shoppingFood.getCategory();
        if(category != null) {
            foodDTO.setCategoryId(category.getId());
            foodDTO.setCategoryName(category.getName());
        }
        return foodDTO;
    }


    @GetMapping("/by-user")
    public ResponseEntity<List<FoodDTO>> getShoppingFoodByUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);

        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<ShoppingFood> shoppingFoodList = shoppingFoodService.getShoppingFoodByUser(currentUser);
        List<FoodDTO> foodDTOList = shoppingFoodList.stream().map(shoppingFood -> mapToDTO(shoppingFood)).collect(Collectors.toList());
        return new ResponseEntity<>(foodDTOList, HttpStatus.OK);
    }

    @GetMapping("/by-category/{userId}/{categoryId}")
    public ResponseEntity<List<FoodDTO>> getShoppingFoodByCategory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long userId,
            @PathVariable Long categoryId
    ) {
        User user = userRepository.findById(userId).orElse(null);
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if(user == null || category == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<ShoppingFood> foodList = shoppingFoodService.getShoppingFoodByUserAndCategory(user, category);
        List<FoodDTO> foodDTOList = foodList.stream().map(this::mapToDTO).collect(Collectors.toList());
        return new ResponseEntity<>(foodDTOList,HttpStatus.OK);
    }


    @PostMapping("/food-stock")
    public ResponseEntity<List<ShoppingFood>> shoppingFoodStock(
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

        List<ShoppingFood> shoppingFoodList = new ArrayList<>();
        for (FoodDTO foodDTO : foodDTOList) {
            ShoppingFood shoppingFood = new ShoppingFood();
            shoppingFood.setName(foodDTO.getName());
            shoppingFood.setQuantity(foodDTO.getQuantity());
            shoppingFood.setDate(foodDTO.getDate());
            shoppingFood.setUser(currentUser);

            Long categoryId = foodDTO.getCategoryId();
            if (categoryId != null) {
                Category storedCategory = categoryRepository.findById(categoryId).orElse(null);
                if (storedCategory == null) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                shoppingFood.setCategory(storedCategory);
            }

            shoppingFoodList.add(shoppingFood);
        }

        List<ShoppingFood> savedShoppingFoodList = shoppingFoodService.saveAll(shoppingFoodList);

        return new ResponseEntity<>(savedShoppingFoodList, HttpStatus.OK);
    }


    @PutMapping("/food-update")
    public ResponseEntity<List<FoodDTO>> updatedShoppingFoodStock(
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


        List<ShoppingFood> updatedShoppingFoodList = new ArrayList<>();
        for (FoodDTO foodDTO : foodDTOList) {
            Long foodId = foodDTO.getId();
            if (foodId != null) {
                Optional<ShoppingFood> optionalShoppingFood = shoppingFoodService.GetById(foodId);
                if (optionalShoppingFood.isPresent()) {
                    ShoppingFood existingFood = optionalShoppingFood.get();
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
                    updatedShoppingFoodList.add(existingFood);
                }
            }
        }
        List<ShoppingFood> savedShoppingFoodList = shoppingFoodService.saveAll(updatedShoppingFoodList);

        List<FoodDTO> savedShoppingFoodDTOList = savedShoppingFoodList.stream().map(this::mapToDTO).collect(Collectors.toList());

        return new ResponseEntity<>(savedShoppingFoodDTOList, HttpStatus.OK);
    }


    @DeleteMapping("/food-delete/{id}")
    public ResponseEntity<String> shoppingFoodDelete(@PathVariable("id") Long id,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);

        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<ShoppingFood> optionalShoppingFood = shoppingFoodService.GetById(id);

        if (optionalShoppingFood.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ShoppingFood shoppingFood = optionalShoppingFood.get();
        if (!shoppingFood.getUser().equals(currentUser)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        shoppingFoodService.delete(id);

        return new ResponseEntity<>("ShoppingFood deleted successfully!", HttpStatus.OK);

    }
}
