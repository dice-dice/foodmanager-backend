package com.spire.fridge.inventory.service;

import com.spire.fridge.inventory.dto.stats.*;
import com.spire.fridge.inventory.entity.Food;
import com.spire.fridge.inventory.entity.User;
import com.spire.fridge.inventory.repository.FoodRepository;
import com.spire.fridge.inventory.repository.ShoppingFoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StatsService {

    private final FoodRepository foodRepository;
    private final ShoppingFoodRepository shoppingFoodRepository;

    @Autowired
    public StatsService(FoodRepository foodRepository, ShoppingFoodRepository shoppingFoodRepository) {
        this.foodRepository = foodRepository;
        this.shoppingFoodRepository = shoppingFoodRepository;
    }

    public OverviewStatsDTO getOverviewStats(User user) {
        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(3);

        long totalFoods = foodRepository.countByUser(user);
        long expiringIn3Days = foodRepository.countExpiringBetween(user, today, threeDaysLater);
        long expiredCount = foodRepository.countExpired(user, today);
        long shoppingListCount = shoppingFoodRepository.countByUser(user);

        List<CategoryBreakdownDTO> categoryBreakdown = getCategoryBreakdown(user);

        return new OverviewStatsDTO(totalFoods, expiringIn3Days, expiredCount, categoryBreakdown, shoppingListCount);
    }

    public List<CategoryBreakdownDTO> getCategoryBreakdown(User user) {
        List<Object[]> results = foodRepository.countByUserGroupByCategory(user);
        return results.stream()
                .map(row -> new CategoryBreakdownDTO(
                        (Long) row[0],
                        (String) row[1],
                        (Long) row[2]
                ))
                .collect(Collectors.toList());
    }

    public List<ExpirationCalendarDTO> getExpirationCalendar(User user, YearMonth month) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();
        LocalDate today = LocalDate.now();

        List<Food> foods = foodRepository.findByUserAndExpirationDateBetween(user, startDate, endDate);

        Map<LocalDate, List<ExpirationItemDTO>> grouped = foods.stream()
                .collect(Collectors.groupingBy(
                        Food::getExpirationDate,
                        TreeMap::new,
                        Collectors.mapping(food -> {
                            int daysLeft = (int) ChronoUnit.DAYS.between(today, food.getExpirationDate());
                            String categoryName = food.getCategory() != null ? food.getCategory().getName() : null;
                            return new ExpirationItemDTO(food.getId(), food.getName(), daysLeft, categoryName);
                        }, Collectors.toList())
                ));

        return grouped.entrySet().stream()
                .map(entry -> new ExpirationCalendarDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public TrendDataDTO getTrendData(User user, String period, int range) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;
        DateTimeFormatter formatter;

        if ("weekly".equals(period)) {
            startDate = endDate.minusWeeks(range);
            formatter = DateTimeFormatter.ofPattern("M/d");
        } else if ("monthly".equals(period)) {
            startDate = endDate.minusMonths(range);
            formatter = DateTimeFormatter.ofPattern("M月");
        } else {
            startDate = endDate.minusDays(range);
            formatter = DateTimeFormatter.ofPattern("M/d");
        }

        List<Object[]> results = foodRepository.countByUserGroupByDate(user, startDate, endDate);

        Map<LocalDate, Long> dataMap = new LinkedHashMap<>();
        for (Object[] row : results) {
            LocalDate date = (LocalDate) row[0];
            Long count = (Long) row[1];
            dataMap.put(date, count);
        }

        List<String> labels = new ArrayList<>();
        List<Long> added = new ArrayList<>();

        if ("weekly".equals(period)) {
            for (int i = range; i >= 0; i--) {
                LocalDate weekStart = endDate.minusWeeks(i);
                LocalDate weekEnd = weekStart.plusDays(6);
                String label = weekStart.format(formatter) + "週";
                labels.add(label);

                long weekTotal = dataMap.entrySet().stream()
                        .filter(e -> !e.getKey().isBefore(weekStart) && !e.getKey().isAfter(weekEnd))
                        .mapToLong(Map.Entry::getValue)
                        .sum();
                added.add(weekTotal);
            }
        } else if ("monthly".equals(period)) {
            for (int i = range; i >= 0; i--) {
                YearMonth ym = YearMonth.from(endDate.minusMonths(i));
                String label = ym.format(DateTimeFormatter.ofPattern("M月"));
                labels.add(label);

                long monthTotal = dataMap.entrySet().stream()
                        .filter(e -> YearMonth.from(e.getKey()).equals(ym))
                        .mapToLong(Map.Entry::getValue)
                        .sum();
                added.add(monthTotal);
            }
        } else {
            for (int i = range; i >= 0; i--) {
                LocalDate date = endDate.minusDays(i);
                labels.add(date.format(formatter));
                added.add(dataMap.getOrDefault(date, 0L));
            }
        }

        return new TrendDataDTO(labels, added);
    }
}
