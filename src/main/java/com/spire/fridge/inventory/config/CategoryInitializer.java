package com.spire.fridge.inventory.config;

import com.spire.fridge.inventory.entity.Category;
import com.spire.fridge.inventory.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryInitializer implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;
    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            List<String> defaults = List.of(

                    "果物","野菜","肉","魚","乳製品","冷凍","その他"
            );
            for(String name: defaults ) {
                Category c = new Category();
                c.setName(name);
                categoryRepository.save(c);
            }

        }
    }
}
