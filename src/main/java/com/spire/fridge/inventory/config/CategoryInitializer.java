package com.spire.fridge.inventory.config;

import com.spire.fridge.inventory.entity.Category;
import com.spire.fridge.inventory.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("!test")
@Component
public class CategoryInitializer  {

    @Autowired
    private CategoryRepository categoryRepository;
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        if (categoryRepository.count() == 0) {

            List<String> defaults = List.of(
                    "果物","野菜","肉","魚","乳製品","冷凍","日用品","その他"
            );

            for (String name : defaults) {
                Category c = new Category();
                c.setName(name);
                categoryRepository.save(c);
            }
        }
    }
}
