package com.spire.fridge.inventory.repository;

import com.spire.fridge.inventory.entity.Category;
import com.spire.fridge.inventory.entity.Food;
import com.spire.fridge.inventory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByUser(User user);
    List<Food> findByUserAndCategory(User user, Category category);

    // 統計用クエリメソッド
    long countByUser(User user);

    // 期限切れ間近（指定日までに期限が切れる食材）
    @Query("SELECT COUNT(f) FROM Food f WHERE f.user = :user AND f.expirationDate IS NOT NULL AND f.expirationDate BETWEEN :today AND :targetDate")
    long countExpiringBetween(@Param("user") User user, @Param("today") LocalDate today, @Param("targetDate") LocalDate targetDate);

    // 既に期限切れの食材
    @Query("SELECT COUNT(f) FROM Food f WHERE f.user = :user AND f.expirationDate IS NOT NULL AND f.expirationDate < :today")
    long countExpired(@Param("user") User user, @Param("today") LocalDate today);

    // カテゴリ別の食材数
    @Query("SELECT f.category.id, f.category.name, COUNT(f) FROM Food f WHERE f.user = :user AND f.category IS NOT NULL GROUP BY f.category.id, f.category.name")
    List<Object[]> countByUserGroupByCategory(@Param("user") User user);

    // 期限カレンダー用：指定月の期限切れ食材一覧
    @Query("SELECT f FROM Food f WHERE f.user = :user AND f.expirationDate IS NOT NULL AND f.expirationDate BETWEEN :startDate AND :endDate ORDER BY f.expirationDate")
    List<Food> findByUserAndExpirationDateBetween(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 登録日別の食材数（トレンド用）
    @Query("SELECT f.date, COUNT(f) FROM Food f WHERE f.user = :user AND f.date BETWEEN :startDate AND :endDate GROUP BY f.date ORDER BY f.date")
    List<Object[]> countByUserGroupByDate(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
